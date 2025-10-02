package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.PerformanceReport;
import com.ftn.sbnz.model.Rating;
import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.model.Server;
import com.ftn.sbnz.model.User;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

@Service
public class DroolsService {

    private static final Logger log = LoggerFactory.getLogger(DroolsService.class);
    private final KieSession kieSession;

    @Autowired
    public DroolsService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public void registerRental(Rental rental) {
        if (rental == null || rental.getId() == null) {
            throw new IllegalArgumentException("Rental must have an id before syncing with Drools");
        }

        User userFact = ensureUserFact(rental.getUser());
        Server serverFact = ensureServerFact(rental);

        Optional<Rental> existing = findFact(Rental.class, rental.getId());
        if (existing.isPresent()) {
            Rental fact = existing.get();
            fact.setStartDate(rental.getStartDate());
            fact.setEndDate(rental.getEndDate());
            fact.setPurpose(rental.getPurpose());
            fact.setDurationDays(rental.getDurationDays());
            fact.setServer(serverFact);
            fact.setRatingScore(rental.getRatingScore());
            fact.setRatedAt(rental.getRatedAt());
            FactHandle handle = kieSession.getFactHandle(fact);
            if (handle != null) {
                kieSession.update(handle, fact);
            }
            log.info("Updated existing rental fact [{}] in Drools", rental.getId());
        } else {
            Rental fact = new Rental(rental.getId(), userFact, serverFact,
                    rental.getStartDate(), rental.getEndDate(), rental.getPurpose(), rental.getDurationDays());
            fact.setRatingScore(rental.getRatingScore());
            fact.setRatedAt(rental.getRatedAt());
            kieSession.insert(fact);
            log.info("Inserted new rental fact [{}] into Drools", rental.getId());
        }

        fireRulesAndDrainReports();
    }

    public void registerRating(Rental rental, int score) {
        if (rental == null || rental.getId() == null) {
            throw new IllegalArgumentException("Rental must have an id before rating");
        }

        registerRental(rental);

        User userFact = ensureUserFact(rental.getUser());
        Server serverFact = ensureServerFact(rental);

        Rental rentalFact = findFact(Rental.class, rental.getId())
                .orElseThrow(() -> new IllegalStateException("Rental fact not found in Drools"));

        rentalFact.setEndDate(rental.getEndDate());
        rentalFact.setRatingScore(score);
        rentalFact.setRatedAt(rental.getRatedAt());
        FactHandle handle = kieSession.getFactHandle(rentalFact);
        if (handle != null) {
            kieSession.update(handle, rentalFact);
        }

        Rating ratingFact = new Rating(
                System.currentTimeMillis(),
                userFact,
                serverFact,
                score,
                rental.getRatedAt() != null ? rental.getRatedAt() : new Date(),
                rentalFact
        );

        kieSession.insert(ratingFact);
        log.info("Inserted rating for rental [{}] with score {}", rental.getId(), score);

        fireRulesAndDrainReports();
    }

    private User ensureUserFact(User source) {
        if (source == null || source.getId() == null) {
            throw new IllegalArgumentException("Rental user is missing");
        }
        return findFact(User.class, source.getId())
                .orElseGet(() -> {
                    User fact = new User(source.getId(), source.getUsername());
                    fact.setStatus(source.getStatus());
                    if (source.getRoles() != null) {
                        fact.setRoles(new HashSet<>(source.getRoles()));
                    }
                    kieSession.insert(fact);
                    log.debug("Inserted user [{}] fact into Drools", source.getId());
                    return fact;
                });
    }

    private Server ensureServerFact(Rental rental) {
        Long serverId = null;
        if (rental.getServiceOffering() != null) {
            serverId = rental.getServiceOffering().getId();
        } else if (rental.getServer() != null) {
            serverId = rental.getServer().getId();
        }
        if (serverId == null) {
            throw new IllegalArgumentException("Rental server context is missing");
        }

        final Long id = serverId;
        Optional<Server> existing = findFact(Server.class, id);
        if (existing.isPresent()) {
            return existing.get();
        }

        Server serverFact;
        if (rental.getServiceOffering() != null) {
            serverFact = rental.getServiceOffering().toServer();
        } else if (rental.getServer() != null) {
            serverFact = rental.getServer();
        } else {
            throw new IllegalArgumentException("Unable to resolve server fact for rental " + rental.getId());
        }

        if (serverFact.getId() == null) {
            serverFact.setId(id);
        }

        kieSession.insert(serverFact);
        log.debug("Inserted server [{}] fact into Drools", id);
        return serverFact;
    }

    private void fireRulesAndDrainReports() {
        kieSession.fireAllRules();

        for (FactHandle handle : kieSession.getFactHandles(obj -> obj instanceof PerformanceReport)) {
            PerformanceReport report = (PerformanceReport) kieSession.getObject(handle);
            if (report.getMessage() != null && report.getMessage().toLowerCase().contains("critically low")) {
                log.warn("ADMIN REPORT: {}", report.getMessage());
            } else {
                log.info("ADMIN REPORT: {}", report.getMessage());
            }
            kieSession.delete(handle);
        }
    }

    private <T> Optional<T> findFact(Class<T> factClass, Long id) {
        return kieSession.getObjects(obj -> factClass.isInstance(obj) && hasId(obj, id))
                .stream()
                .map(factClass::cast)
                .findFirst();
    }

    private boolean hasId(Object obj, Long id) {
        try {
            return id.equals(obj.getClass().getMethod("getId").invoke(obj));
        } catch (Exception e) {
            return false;
        }
    }
}
