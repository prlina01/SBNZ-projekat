package com.ftn.sbnz.service.repositories;

import com.ftn.sbnz.model.catalog.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    boolean existsByNameIgnoreCaseAndProviderNameIgnoreCase(String name, String providerName);
    boolean existsByNameIgnoreCaseAndProviderNameIgnoreCaseAndIdNot(String name, String providerName, Long id);

    @Query(value = "SELECT * FROM service_offerings ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<ServiceOffering> findRandom(@Param("limit") int limit);
}
