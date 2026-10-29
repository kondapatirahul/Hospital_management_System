package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.OperationTheatre;
import com.healthcare.hospital.enums.TheatreStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationTheatreRepository extends JpaRepository<OperationTheatre, Long> {

    Optional<OperationTheatre> findByTheatreCodeIgnoreCase(String theatreCode);

    boolean existsByTheatreCodeIgnoreCase(String theatreCode);

    List<OperationTheatre> findByActiveTrueOrderByTheatreCodeAsc();

    List<OperationTheatre> findByActiveTrueAndStatusOrderByTheatreCodeAsc(TheatreStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ot FROM OperationTheatre ot WHERE ot.id = :id")
    Optional<OperationTheatre> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT ot FROM OperationTheatre ot WHERE " +
           "(:search IS NULL OR LOWER(ot.theatreCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(ot.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(ot.location) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(ot.theatreType) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR ot.status = :status) " +
           "AND (:activeOnly = false OR ot.active = true)")
    Page<OperationTheatre> searchTheatres(@Param("search") String search,
                                         @Param("status") TheatreStatus status,
                                         @Param("activeOnly") boolean activeOnly,
                                         Pageable pageable);

    long countByStatus(TheatreStatus status);

    long countByActiveTrue();
}
