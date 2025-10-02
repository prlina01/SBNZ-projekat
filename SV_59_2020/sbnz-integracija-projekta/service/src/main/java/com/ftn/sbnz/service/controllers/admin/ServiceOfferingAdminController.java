package com.ftn.sbnz.service.controllers.admin;

import com.ftn.sbnz.model.catalog.ServiceOffering;
import com.ftn.sbnz.service.catalog.dto.ServiceOfferingRequest;
import com.ftn.sbnz.service.catalog.dto.ServiceOfferingResponse;
import com.ftn.sbnz.service.services.catalog.ServiceOfferingMapper;
import com.ftn.sbnz.service.services.catalog.ServiceOfferingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/services")
@Validated
public class ServiceOfferingAdminController {

    private final ServiceOfferingService service;
    private final ServiceOfferingMapper mapper;

    public ServiceOfferingAdminController(ServiceOfferingService service, ServiceOfferingMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ServiceOfferingResponse> listAll() {
        Map<Long, Long> activeCounts = service.getActiveRentalCounts();
        Map<Long, Double> averageRatings = service.getAverageRatings();
        return service.findAll().stream()
                .map(offering -> {
                    ServiceOfferingResponse response = mapper.toResponse(offering);
                    response.setActiveRentalCount(activeCounts.getOrDefault(offering.getId(), 0L));
                    response.setAverageRating(averageRatings.get(offering.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ServiceOfferingResponse getById(@PathVariable Long id) {
        ServiceOffering offering = service.findById(id);
        ServiceOfferingResponse response = mapper.toResponse(offering);
        response.setActiveRentalCount(service.getActiveRentalCount(id));
        response.setAverageRating(service.getAverageRating(id));
        return response;
    }

    @PostMapping
    public ResponseEntity<ServiceOfferingResponse> create(@Valid @RequestBody ServiceOfferingRequest request) {
        ServiceOffering created = service.create(request);
        ServiceOfferingResponse response = mapper.toResponse(created);
        response.setActiveRentalCount(service.getActiveRentalCount(response.getId()));
        response.setAverageRating(service.getAverageRating(response.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ServiceOfferingResponse update(@PathVariable Long id, @Valid @RequestBody ServiceOfferingRequest request) {
        ServiceOffering updated = service.update(id, request);
        ServiceOfferingResponse response = mapper.toResponse(updated);
        response.setActiveRentalCount(service.getActiveRentalCount(id));
        response.setAverageRating(service.getAverageRating(id));
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<String> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
