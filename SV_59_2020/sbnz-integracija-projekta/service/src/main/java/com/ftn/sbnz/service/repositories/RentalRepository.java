package com.ftn.sbnz.service.repositories;

import com.ftn.sbnz.model.Rental;
import com.ftn.sbnz.model.RentalStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    @EntityGraph(attributePaths = {"serviceOffering", "user"})
    List<Rental> findByUserUsernameOrderByStartDateDesc(String username);

    @EntityGraph(attributePaths = {"serviceOffering", "user"})
    List<Rental> findByUserUsernameOrderByRequestedAtDesc(String username);

    @EntityGraph(attributePaths = {"serviceOffering", "user"})
    List<Rental> findByStatusOrderByRequestedAtAsc(RentalStatus status);

    Optional<Rental> findByIdAndUserUsername(Long id, String username);

    @Query(value = "SELECT COUNT(*) "
        + "FROM rentals r "
        + "WHERE r.service_offering_id = :serviceId "
        + "  AND r.status = 'ACTIVE' "
        + "  AND COALESCE(r.end_date, r.start_date + (r.duration_days * INTERVAL '1 day')) > CURRENT_TIMESTAMP",
        nativeQuery = true)
    long countActiveRentalsForService(@Param("serviceId") Long serviceOfferingId);

    @Query(value = "SELECT r.service_offering_id, COUNT(*) "
        + "FROM rentals r "
        + "WHERE r.status = 'ACTIVE' "
        + "  AND COALESCE(r.end_date, r.start_date + (r.duration_days * INTERVAL '1 day')) > CURRENT_TIMESTAMP "
        + "GROUP BY r.service_offering_id",
        nativeQuery = true)
    List<Object[]> countActiveRentalsPerService();

    @Query(value = "SELECT AVG(r.rating_score) "
        + "FROM rentals r "
        + "WHERE r.service_offering_id = :serviceId AND r.rating_score IS NOT NULL",
        nativeQuery = true)
    Double findAverageRatingForService(@Param("serviceId") Long serviceOfferingId);

    @Query(value = "SELECT r.service_offering_id, AVG(r.rating_score) "
        + "FROM rentals r "
        + "WHERE r.rating_score IS NOT NULL "
        + "GROUP BY r.service_offering_id",
        nativeQuery = true)
    List<Object[]> findAverageRatingsPerService();
}
