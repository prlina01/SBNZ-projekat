package com.ftn.sbnz.service;

import com.ftn.sbnz.model.*;
import com.ftn.sbnz.service.dto.RatingDTO;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class DroolsService {

    private static final Logger log = LoggerFactory.getLogger(DroolsService.class);
    private final KieSession kieSession;

    @Autowired
    public DroolsService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public void addRating(RatingDTO ratingDTO) {
        // Find User, Server, and Rental from the session
        Optional<User> user = findFact(User.class, ratingDTO.getUserId());
        Optional<Server> server = findFact(Server.class, ratingDTO.getServerId());
        Optional<Rental> rental = findFact(Rental.class, ratingDTO.getRentalId());

        if (user.isPresent() && server.isPresent() && rental.isPresent()) {
            Rating newRating = new Rating(
                    (long) (Math.random() * 1000), // Generate a random ID for the rating
                    user.get(),
                    server.get(),
                    ratingDTO.getScore(),
                    new Date(),
                    rental.get()
            );

            log.info("Inserting new rating: {}", newRating);
            kieSession.insert(newRating);

            // Fire rules and log reports
            int firedRules = kieSession.fireAllRules();
            log.info("Fired {} rules.", firedRules);

            // Clean up reports after logging
            for (FactHandle handle : kieSession.getFactHandles(obj -> obj instanceof PerformanceReport)) {
                PerformanceReport report = (PerformanceReport) kieSession.getObject(handle);
                log.warn("ADMIN REPORT: {}", report.getMessage());
                kieSession.delete(handle); // Remove the report to avoid re-logging
            }
        } else {
            log.error("Could not find all required facts to create a rating.");
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
