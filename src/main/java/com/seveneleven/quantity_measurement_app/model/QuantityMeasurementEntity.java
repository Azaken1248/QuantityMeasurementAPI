package com.seveneleven.quantity_measurement_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * UC 16 Enhancement:
 * Refactoring QuantityMeasurementEntity - Refactor the QuantityMeasurementEntity class
 * to improve its design and maintainability. This includes adding JPA annotations for
 * database mapping, implementing constructors for different use cases, and ensuring that
 * the class is properly annotated with Lombok to reduce boilerplate code. The refactored
 * class should be designed to be easily persisted in a database, with appropriate
 * indexing for efficient querying of quantity measurement operations and results.
 */
@Entity
@Table(name = "quantity_measurement_entity", indexes = {
        @Index(name = "idx_operation", columnList = "operation"),
        @Index(name = "idx_measurement_type", columnList = "this_measurement_type"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "this_value", nullable = false)
    public double thisValue;

    @Column(name = "this_unit", nullable = false)
    public String thisUnit;

    @Column(name = "this_measurement_type", nullable = false)
    public String thisMeasurementType;

    @Column(name = "that_value", nullable = false)
    public double thatValue;

    @Column(name = "that_unit", nullable = false)
    public String thatUnit;

    @Column(name = "that_measurement_type", nullable = false)
    public String thatMeasurementType;

    @Column(name = "operation", nullable = false)
    public String operation;

    @Column(name = "result_value")
    public double resultValue;

    @Column(name = "result_unit")
    public String resultUnit;

    @Column(name = "result_measurement_type")
    public String resultMeasurementType;

    @Column(name = "result_string")
    public String resultString;

    @Column(name = "is_error")
    public boolean isError;

    @Column(name = "error_message")
    public String errorMessage;

    @Column(name = "comparison_result")
    @Enumerated(EnumType.STRING)
    public ComparisonResult comparisonResult;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public QuantityMeasurementEntity(QuantityDTO thisQuantity, QuantityDTO thatQuantity, String operation, Object result) {
        this.thisValue = thisQuantity.getValue();
        this.thisUnit = thisQuantity.getUnit();
        this.thisMeasurementType = thisQuantity.getMeasurementType();

        if (thatQuantity != null) {
            this.thatValue = thatQuantity.getValue();
            this.thatUnit = thatQuantity.getUnit();
            this.thatMeasurementType = thatQuantity.getMeasurementType();
        }

        this.operation = operation;

        if (result instanceof String) {
            this.resultString = (String) result;
        } else if (result instanceof Double) {
            this.resultValue = (Double) result;
        } else if (result instanceof ComparisonResult) {
            this.comparisonResult = (ComparisonResult) result;
        }

        this.isError = false;
    }

    public QuantityMeasurementEntity(QuantityDTO thisQuantity, QuantityDTO thatQuantity, String operation, QuantityDTO result) {
        this.thisValue = thisQuantity.getValue();
        this.thisUnit = thisQuantity.getUnit();
        this.thisMeasurementType = thisQuantity.getMeasurementType();

        this.thatValue = thatQuantity.getValue();
        this.thatUnit = thatQuantity.getUnit();
        this.thatMeasurementType = thatQuantity.getMeasurementType();

        this.operation = operation;

        if (result != null) {
            this.resultValue = result.getValue();
            this.resultUnit = result.getUnit();
            this.resultMeasurementType = result.getMeasurementType();
        }

        this.isError = false;
    }

    public QuantityMeasurementEntity(QuantityDTO thisQuantity, QuantityDTO thatQuantity, String operation, String errorMessage, boolean isError) {
        if (thisQuantity != null) {
            this.thisValue = thisQuantity.getValue();
            this.thisUnit = thisQuantity.getUnit();
            this.thisMeasurementType = thisQuantity.getMeasurementType();
        }

        if (thatQuantity != null) {
            this.thatValue = thatQuantity.getValue();
            this.thatUnit = thatQuantity.getUnit();
            this.thatMeasurementType = thatQuantity.getMeasurementType();
        }

        this.operation = operation;
        this.errorMessage = errorMessage;
        this.isError = isError;
    }
}
