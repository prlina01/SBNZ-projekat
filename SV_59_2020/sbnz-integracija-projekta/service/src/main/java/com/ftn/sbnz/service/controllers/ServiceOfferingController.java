package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.service.catalog.dto.ServiceOfferingResponse;
import com.ftn.sbnz.service.services.catalog.ServiceOfferingMapper;
import com.ftn.sbnz.service.services.catalog.ServiceOfferingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
@Validated
public class ServiceOfferingController {

    private final ServiceOfferingService service;
    private final ServiceOfferingMapper mapper;

    public ServiceOfferingController(ServiceOfferingService service, ServiceOfferingMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ServiceOfferingResponse> listAll() {
        return service.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ServiceOfferingResponse getById(@PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @GetMapping("/featured")
    public List<ServiceOfferingResponse> featured(@RequestParam(name = "count", defaultValue = "2") int count) {
        return service.findFeatured(count).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
