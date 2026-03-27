package com.seveneleven.quantity_measurement_app.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.seveneleven.quantity_measurement_app.model.QuantityMeasurementEntity;

/**
 * Quantity Measurement Repository Interface - This is completely refactored to use
 * Spring Data JPA. It extends JpaRepository, which provides built-in CRUD operations
 * and query methods. We have defined custom query methods based on method naming
 * conventions and using @Query annotations for more complex queries.
 *
 * Use of JPA eliminates the need for manual SQL and JDBC code, allowing us to focus
 * on business logic and leverage the powerful features of Spring Data JPA for data
 * access and manipulation.
 *
 * Refactoring QuantityMeasurementRepository - Refactor the QuantityMeasurementRepository
 * interface to extend Spring Data JPA's JpaRepository. This involves defining the
 * repository interface to extend JpaRepository<QuantityMeasurementEntity, Long>, which
 * provides built-in CRUD operations and query methods. We will also define custom query
 * methods based on method naming conventions or using @Query annotations for more complex
 * queries.
 *
 * The refactored repository will include methods for finding measurements by operation
 * type, measurement type, and creation date, as well as custom queries for successful
 * operations and counting records based on specific criteria. This will enhance the
 * maintainability and scalability of the data access layer in our application.
 */
@Repository // Marks this interface as a Spring Data Repository
public interface QuantityMeasurementRepository 
        extends JpaRepository<QuantityMeasurementEntity, Long> 
{
    // Find all measurements by operation type
    List<QuantityMeasurementEntity> findByOperation(String operation);

    // Find all measurements by measurement type
    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);

    // Find measurements created after specific date
    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);

    // Custom JPQL query for complex operations
    @Query("SELECT e FROM QuantityMeasurementEntity e WHERE e.operation = :operation " +
           "AND e.isError = false")
    List<QuantityMeasurementEntity> findSuccessfulOperations(
        @Param("operation") String operation
    );

    // Count successful operations
    long countByOperationAndIsErrorFalse(String operation);

    // Find measurements with errors
    List<QuantityMeasurementEntity> findByIsErrorTrue();
}