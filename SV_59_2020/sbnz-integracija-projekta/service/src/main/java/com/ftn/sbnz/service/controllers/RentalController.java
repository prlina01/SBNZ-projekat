package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.service.dto.RentRequest;
import com.ftn.sbnz.service.dto.RentalRatingRequest;
import com.ftn.sbnz.service.dto.RentalSummaryResponse;
import com.ftn.sbnz.service.services.rental.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@Validated
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<RentalSummaryResponse> createRental(@Valid @RequestBody RentRequest request,
                                                             Authentication authentication) {
        String username = authentication.getName();
        RentalSummaryResponse response = rentalService.rent(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RentalSummaryResponse>> listMyRentals(Authentication authentication) {
        String username = authentication.getName();
        List<RentalSummaryResponse> rentals = rentalService.listForUser(username);
        return ResponseEntity.ok(rentals);
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<RentalSummaryResponse> rateRental(@PathVariable Long id,
                                                            @Valid @RequestBody RentalRatingRequest request,
                                                            Authentication authentication) {
        String username = authentication.getName();
        RentalSummaryResponse response = rentalService.rate(username, id, request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
