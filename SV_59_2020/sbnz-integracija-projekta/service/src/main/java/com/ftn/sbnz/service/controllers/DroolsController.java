package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.service.dto.RatingDTO;
import com.ftn.sbnz.service.dto.RentalDTO;
import com.ftn.sbnz.service.dto.RentalSummaryResponse;
import com.ftn.sbnz.service.repositories.RentalRepository;
import com.ftn.sbnz.service.services.rental.RentalService;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/legacy")
@Deprecated
public class DroolsController {

    private final RentalService rentalService;
    private final RentalRepository rentalRepository;

    public DroolsController(RentalService rentalService, RentalRepository rentalRepository) {
        this.rentalService = rentalService;
        this.rentalRepository = rentalRepository;
    }

    @PostMapping("/ratings")
    public ResponseEntity<Void> addRating(@RequestBody RatingDTO ratingDTO,
                                          Authentication authentication) {
        rentalService.rate(authentication.getName(), ratingDTO.getRentalId(), ratingDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rentals")
    public ResponseEntity<Rental> createRental(@RequestBody RentalDTO rentalDTO,
                                               Authentication authentication) {
        try {
            RentalSummaryResponse response = rentalService.rent(authentication.getName(), rentalDTO);
            if (response.getId() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            Rental rental = rentalRepository.findById(response.getId()).orElse(null);
            if (rental == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(rental);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
