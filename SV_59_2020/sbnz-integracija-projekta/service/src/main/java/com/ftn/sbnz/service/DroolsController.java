package com.ftn.sbnz.service;

import com.ftn.sbnz.service.dto.RatingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DroolsController {

    private final DroolsService droolsService;

    @Autowired
    public DroolsController(DroolsService droolsService) {
        this.droolsService = droolsService;
    }

    @PostMapping("/ratings")
    public ResponseEntity<Void> addRating(@RequestBody RatingDTO ratingDTO) {
        droolsService.addRating(ratingDTO);
        return ResponseEntity.ok().build();
    }
}
