package com.seveneleven.quantity_measurement_app.service;

import java.util.List;
import com.seveneleven.quantity_measurement_app.model.QuantityDTO;
import com.seveneleven.quantity_measurement_app.model.QuantityMeasurementDTO;

/**
 * IQuantityMeasurementService interface modifications for UC17: You will notice that
 * practically at broad level all the methods are same only some following changes are
 * made to accomodate the new requirements of UC17:
 * 1. Added new methods to retrieve operation history and count operations based on type.
 * 2. Added new method to retrieve error history of operations that resulted in errors.
 * 3. Updated method signatures to return lists of QuantityMeasurementDTO for history retrieval.
 * 4. Added JavaDoc comments to new methods for clarity and documentation.
 */
public interface IQuantityMeasurementService {

    public QuantityMeasurementDTO compare(
            QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    public QuantityMeasurementDTO convert(
            QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    public QuantityMeasurementDTO add(
            QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    public QuantityMeasurementDTO add(
            QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
            QuantityDTO targetUnitDTO
    );

    public QuantityMeasurementDTO subtract(
            QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    public QuantityMeasurementDTO subtract(
            QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
            QuantityDTO targetUnitDTO
    );

    public QuantityMeasurementDTO divide(
            QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    /**
     * Retrieve the history of quantity measurement operations for a specific operation type.
     *
     * @param operation the type of operation for which to retrieve the history
     * (e.g., "conversion", "comparison")
     * @return a list of {@code QuantityMeasurementDTO} representing the history of
     * operations for the specified type
     */
    List<QuantityMeasurementDTO> getOperationHistory(String operation);

    /**
     * Retrieve the history of quantity measurement operations for a specific
     * measurement type.
     *
     * @param type the measurement type for which to retrieve the history (e.g.,
     * "length", "weight", "volume", "temperature")
     * @return a list of {@code QuantityMeasurementDTO} representing the history
     * of operations for the specified type
     */
    List<QuantityMeasurementDTO> getMeasurementsByType(String type);

    /**
     * Get the count of quantity measurement operations for a specific operation type.
     *
     * @param operation the type of operation for which to count operations
     * @return the number of operations of the specified type
     */
    long getOperationCount(String operation);

    /**
     * Retrieve the history of quantity measurement operations that resulted in errors.
     *
     * @return a list of {@code QuantityMeasurementDTO} representing the history of
     * operations that resulted in errors
     */
    List<QuantityMeasurementDTO> getErrorHistory();

}