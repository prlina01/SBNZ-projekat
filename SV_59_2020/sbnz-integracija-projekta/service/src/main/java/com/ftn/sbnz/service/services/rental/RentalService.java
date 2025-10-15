package com.ftn.sbnz.service.services.rental;

import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.model.RentalStatus;
import com.ftn.sbnz.model.User;
import com.ftn.sbnz.model.catalog.ServiceOffering;
import com.ftn.sbnz.service.dto.RentRequest;
import com.ftn.sbnz.service.dto.RentalApprovalResponse;
import com.ftn.sbnz.service.dto.RentalRatingRequest;
import com.ftn.sbnz.service.dto.RentalSummaryResponse;
import com.ftn.sbnz.service.repositories.RentalRepository;
import com.ftn.sbnz.service.repositories.ServiceOfferingRepository;
import com.ftn.sbnz.service.repositories.UserRepository;
import com.ftn.sbnz.service.services.DroolsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final DroolsService droolsService;

    public RentalService(RentalRepository rentalRepository,
                         UserRepository userRepository,
                         ServiceOfferingRepository serviceOfferingRepository,
                         DroolsService droolsService) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.droolsService = droolsService;
    }

    public RentalSummaryResponse rent(String username, RentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ServiceOffering offering = serviceOfferingRepository.findById(request.getServiceOfferingId())
                .orElseThrow(() -> new IllegalArgumentException("Service offering not found"));

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setServiceOffering(offering);
        rental.setPurpose(request.getPurpose());
        rental.setDurationDays(request.getDurationDays());
        rental.setStartDate(null);
        rental.setEndDate(null);
        rental.setRatingScore(null);
        rental.setRatedAt(null);
        rental.setStatus(RentalStatus.PENDING);
        rental.setRequestedAt(new Date());

        Rental saved = rentalRepository.save(rental);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RentalSummaryResponse> listForUser(String username) {
        return rentalRepository.findByUserUsernameOrderByRequestedAtDesc(username).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RentalSummaryResponse rate(String username, Long rentalId, RentalRatingRequest request) {
        Rental rental = rentalRepository.findByIdAndUserUsername(rentalId, username)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException("Only active rentals can be rated");
        }

        if (rental.getRatingScore() != null) {
            throw new IllegalStateException("Rental already rated");
        }

    Date ratedAt = new Date();
    rental.setRatingScore(request.getScore());
    rental.setRatedAt(ratedAt);

        Rental saved = rentalRepository.save(rental);
        droolsService.registerRating(saved, request.getScore());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RentalApprovalResponse> listPendingApprovals() {
        return rentalRepository.findByStatusOrderByRequestedAtAsc(RentalStatus.PENDING).stream()
                .map(this::toApprovalResponse)
                .collect(Collectors.toList());
    }

    public RentalApprovalResponse approve(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new IllegalStateException("Only pending rentals can be approved");
        }

        Date now = new Date();
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setStartDate(now);
        rental.setEndDate(null);
        rental.setRatedAt(null);

        Rental saved = rentalRepository.save(rental);
        droolsService.registerRental(saved);
        return toApprovalResponse(saved);
    }

    public RentalApprovalResponse reject(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new IllegalStateException("Only pending rentals can be rejected");
        }

        rental.setStatus(RentalStatus.REJECTED);
        rental.setStartDate(null);
        rental.setEndDate(new Date());

        Rental saved = rentalRepository.save(rental);
        droolsService.registerRental(saved);
        return toApprovalResponse(saved);
    }

    private RentalSummaryResponse toResponse(Rental rental) {
        RentalSummaryResponse response = new RentalSummaryResponse();
        response.setId(rental.getId());
        ServiceOffering offering = rental.getServiceOffering();
        if (offering != null) {
            response.setServiceOfferingId(offering.getId());
            response.setServiceName(offering.getName());
            response.setProviderName(offering.getProviderName());
        }
        response.setPurpose(rental.getPurpose());
        response.setStartDate(rental.getStartDate());
        response.setEndDate(rental.getEndDate());
        response.setPlannedEndDate(rental.getPlannedEndDate());
        response.setDurationDays(rental.getDurationDays());
        response.setRating(rental.getRatingScore());
        response.setStatus(rental.getStatus() != null ? rental.getStatus().name() : null);
        response.setRequestedAt(rental.getRequestedAt());

        int remainingDays = calculateRemainingDays(rental);
        response.setRemainingDays(remainingDays);

        boolean active = rental.getStatus() == RentalStatus.ACTIVE;
        response.setActive(active);
        response.setRateable(rental.getStatus() == RentalStatus.ACTIVE && rental.getRatingScore() == null);
        return response;
    }

    private RentalApprovalResponse toApprovalResponse(Rental rental) {
        RentalApprovalResponse response = new RentalApprovalResponse();
        response.setId(rental.getId());
        if (rental.getUser() != null) {
            response.setUsername(rental.getUser().getUsername());
        }
        ServiceOffering offering = rental.getServiceOffering();
        if (offering != null) {
            response.setServiceOfferingId(offering.getId());
            response.setServiceName(offering.getName());
            response.setProviderName(offering.getProviderName());
        }
        response.setPurpose(rental.getPurpose());
        response.setDurationDays(rental.getDurationDays());
        response.setRequestedAt(rental.getRequestedAt());
        response.setStatus(rental.getStatus() != null ? rental.getStatus().name() : null);
        return response;
    }

    private int calculateRemainingDays(Rental rental) {
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            return 0;
        }

    Date plannedEnd = rental.getPlannedEndDate();
        if (plannedEnd == null || rental.getStartDate() == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate planned = plannedEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long days = ChronoUnit.DAYS.between(today, planned);
        return (int) Math.max(days, 0);
    }
}
