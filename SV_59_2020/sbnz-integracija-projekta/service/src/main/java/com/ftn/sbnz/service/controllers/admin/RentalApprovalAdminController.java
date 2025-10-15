package com.ftn.sbnz.service.controllers.admin;

import com.ftn.sbnz.service.dto.RentalApprovalResponse;
import com.ftn.sbnz.service.services.rental.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rentals")
@Validated
public class RentalApprovalAdminController {

    private final RentalService rentalService;

    public RentalApprovalAdminController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<RentalApprovalResponse>> listPendingRequests() {
        List<RentalApprovalResponse> payload = rentalService.listPendingApprovals();
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<RentalApprovalResponse> approve(@PathVariable Long id) {
        RentalApprovalResponse response = rentalService.approve(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<RentalApprovalResponse> reject(@PathVariable Long id) {
        RentalApprovalResponse response = rentalService.reject(id);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
