package com.ftn.sbnz.service.services.rental;

import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.model.User;
import com.ftn.sbnz.model.catalog.ServiceOffering;
import com.ftn.sbnz.service.dto.RentRequest;
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
        rental.setStartDate(new Date());
        rental.setEndDate(null);
        rental.setRatingScore(null);
        rental.setRatedAt(null);

        Rental saved = rentalRepository.save(rental);
        droolsService.registerRental(saved);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RentalSummaryResponse> listForUser(String username) {
        return rentalRepository.findByUserUsernameOrderByStartDateDesc(username).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RentalSummaryResponse rate(String username, Long rentalId, RentalRatingRequest request) {
        Rental rental = rentalRepository.findByIdAndUserUsername(rentalId, username)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

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

        int remainingDays = calculateRemainingDays(rental);
        response.setRemainingDays(remainingDays);

    boolean active = remainingDays > 0;
        response.setActive(active);
        response.setRateable(rental.getRatingScore() == null);
        return response;
    }

    private int calculateRemainingDays(Rental rental) {
        Date plannedEnd = rental.getPlannedEndDate();
        if (plannedEnd == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate planned = plannedEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long days = ChronoUnit.DAYS.between(today, planned);
        return (int) Math.max(days, 0);
    }
}
