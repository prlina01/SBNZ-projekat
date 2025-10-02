package com.ftn.sbnz.service.services.catalog;

import com.ftn.sbnz.model.catalog.ServiceOffering;
import com.ftn.sbnz.service.catalog.dto.ServiceOfferingRequest;
import com.ftn.sbnz.service.repositories.RentalRepository;
import com.ftn.sbnz.service.repositories.ServiceOfferingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceOfferingService {

    private final ServiceOfferingRepository repository;
    private final ServiceUsageInspector usageInspector;
    private final RentalRepository rentalRepository;

    public ServiceOfferingService(ServiceOfferingRepository repository,
                                  ServiceUsageInspector usageInspector,
                                  RentalRepository rentalRepository) {
        this.repository = repository;
        this.usageInspector = usageInspector;
        this.rentalRepository = rentalRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceOffering> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ServiceOffering findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service offering with id=" + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<ServiceOffering> findFeatured(int count) {
        int limit = Math.max(1, Math.min(count, 6));
        List<ServiceOffering> random = repository.findRandom(limit);
        if (random.isEmpty()) {
            return repository.findAll(PageRequest.of(0, limit)).getContent();
        }
        return random;
    }

    public ServiceOffering create(ServiceOfferingRequest request) {
        if (repository.existsByNameIgnoreCaseAndProviderNameIgnoreCase(request.getName(), request.getProvider())) {
            throw new IllegalArgumentException("Service offering with the same name and provider already exists");
        }
        ServiceOffering offering = new ServiceOffering();
        applyRequest(offering, request);
        return repository.save(offering);
    }

    public ServiceOffering update(Long id, ServiceOfferingRequest request) {
        ServiceOffering offering = findById(id);
        if (repository.existsByNameIgnoreCaseAndProviderNameIgnoreCaseAndIdNot(request.getName(), request.getProvider(), id)) {
            throw new IllegalArgumentException("Service offering with the same name and provider already exists");
        }
        applyRequest(offering, request);
        return repository.save(offering);
    }

    public void delete(Long id) {
        ServiceOffering offering = findById(id);
        if (usageInspector.isOfferingInUse(id)) {
            throw new IllegalStateException("Service offering is currently in use and cannot be deleted");
        }
        repository.delete(offering);
    }

    @Transactional(readOnly = true)
    public long getActiveRentalCount(Long serviceId) {
    return rentalRepository.countActiveRentalsForService(serviceId);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getActiveRentalCounts() {
        return rentalRepository.countActiveRentalsPerService().stream()
        .filter(row -> row != null && row.length >= 2 && row[0] != null && row[1] != null)
        .collect(Collectors.toMap(
            row -> ((Number) row[0]).longValue(),
            row -> ((Number) row[1]).longValue(),
            (existing, replacement) -> existing
        ));
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long serviceId) {
    return rentalRepository.findAverageRatingForService(serviceId);
    }

    @Transactional(readOnly = true)
    public Map<Long, Double> getAverageRatings() {
    return rentalRepository.findAverageRatingsPerService().stream()
        .filter(row -> row != null && row.length >= 2 && row[0] != null && row[1] != null)
        .collect(Collectors.toMap(
            row -> ((Number) row[0]).longValue(),
            row -> ((Number) row[1]).doubleValue(),
            (existing, replacement) -> existing
        ));
    }

    private void applyRequest(ServiceOffering offering, ServiceOfferingRequest request) {
        offering.setName(request.getName());
        offering.setProviderName(request.getProvider());
        offering.setPurpose(request.getPurpose());
        offering.setCpuPerformance(request.getCpuPerformance());
        offering.setVcpuCount(request.getVcpuCount());
        offering.setGpuModel(request.getGpuModel());
        offering.setGpuVram(request.getGpuVram());
        offering.setRam(request.getRam());
        offering.setStorageCapacity(request.getStorageCapacity());
        offering.setStorageType(request.getStorageType());
        offering.setEncryptedStorage(request.isEncryptedStorage());
        offering.setNetworkBandwidth(request.getNetworkBandwidth());
        offering.setDdosProtection(request.isDdosProtection());
        offering.setHighAvailability(request.isHighAvailability());
        offering.setRegion(request.getRegion());
        offering.setEcoFriendly(request.isEcoFriendly());
        offering.setPricePerHour(request.getPricePerHour());
        offering.setPricePerMonth(request.getPricePerMonth());
    }
}
