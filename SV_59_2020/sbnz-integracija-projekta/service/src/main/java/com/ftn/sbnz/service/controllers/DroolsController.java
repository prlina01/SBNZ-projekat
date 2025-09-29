package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.service.dto.RatingDTO;
import com.ftn.sbnz.service.dto.RentalDTO;
import com.ftn.sbnz.service.services.DroolsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DroolsController {

    private final DroolsService droolsService;

    public DroolsController(DroolsService droolsService) {
        this.droolsService = droolsService;
    }

    @PostMapping("/ratings")
    public ResponseEntity<Void> addRating(@RequestBody RatingDTO ratingDTO) {
        droolsService.addRating(ratingDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rentals")
    public ResponseEntity<Rental> createRental(@RequestBody RentalDTO rentalDTO) {
        try {
            Rental newRental = droolsService.createRental(rentalDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newRental);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
