package com.seveneleven.quantity_measurement_app.model;

import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * QuantityMeasurementDTO is a Data Transfer Object (DTO) class that serves as a
 * data carrier for quantity measurement operations. It encapsulates all the necessary
 * information related to a quantity measurement operation, including the values and
 * units of the operands, the type of operation being performed, the result of the
 * operation, and any error information if applicable. This class is designed to be
 * used in the service layer and REST controllers to facilitate communication between
 * different layers of the application while maintaining a clear separation of concerns.
 *
 * The fields in this class include:
 * - `thisValue`: The value of the first quantity operand.
 * - `thisUnitName`: The unit name of the first quantity operand.
 * - `thisMeasurementType`: The measurement type of the first quantity operand
 * - `thatValue`: The value of the second quantity operand.
 * - `thatUnitName`: The unit name of the second quantity operand.
 * - `thatMeasurementType`: The measurement type of the second quantity operand.
 * - `operation`: The type of operation being performed (e.g., "conversion", "comparison").
 * - `resultString`: A string representation of the result of the operation.
 * - `resultValue`: The numeric value of the result of the operation.
 * - `resultUnit`: The unit of the result of the operation.
 * - `resultMeasurementType`: The measurement type of the result of the operation.
 * - `isError`: A boolean flag indicating whether an error occurred during the operation.
 * - `errorMessage`: A string containing the error message if an error occurred.
 * This class is annotated with Lombok's `@Data` annotation to automatically generate getters,
 * setters, and other utility methods, reducing boilerplate code and improving maintainability.
 *
 * @author Developer
 * @version 17.0
 * @since 17.0
 */
public @Data class QuantityMeasurementDTO {
    public double thisValue;
    public String thisUnit;
    public String thisMeasurementType;
    public double thatValue;
    public String thatUnit;
    public String thatMeasurementType;
    public String operation;
    public String resultString;
    public double resultValue;
    public String resultUnit;
    public String resultMeasurementType;
    public String errorMessage;

    // ✅ FIX 1: Rename field from isError -> error
    //    Lombok generates isError() for boolean isError - Jackson strips "is"
    //    and maps it to JSON key "error". On deserialization it can't find
    //    "error" -> "isError" and crashes. Renaming to plain "error" fixes this.
    //
    // ✅ FIX 2: Add @JsonProperty("error") to explicitly tell Jackson
    //    the JSON key name - no more ambiguity between field name and getter name.
    @JsonProperty("error")
    public boolean error;

    /**
     * Convert Entity to DTO using Static Factory Method. This method takes as input a
     * QuantityMeasurementEntity and creates a corresponding QuantityMeasurementDTO
     * by copying the relevant fields. It performs a null check on the input entity to
     * ensure robustness. This approach provides a clear and centralized way to convert
     * between the entity and DTO representations, which is especially useful when
     * retrieving data from the database and preparing it for use in the service layer
     * or for returning in REST API responses.
     *
     * @param entity the QuantityMeasurementEntity to be converted to a DTO
     * @return a QuantityMeasurementDTO representing the data from the input entity
     */
    public static QuantityMeasurementDTO from(QuantityMeasurementEntity entity) {
        if (entity == null) {
            return null;
        }
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue(entity.getThisValue());
        dto.setThisUnit(entity.getThisUnit());
        dto.setThisMeasurementType(entity.getThisMeasurementType());
        dto.setThatValue(entity.getThatValue());
        dto.setThatUnit(entity.getThatUnit());
        dto.setThatMeasurementType(entity.getThatMeasurementType());
        dto.setOperation(entity.getOperation());
        dto.setResultString(entity.getResultString());
        dto.setResultValue(entity.getResultValue());
        dto.setResultUnit(entity.getResultUnit());
        dto.setResultMeasurementType(entity.getResultMeasurementType());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setError(entity.isError()); 
        return dto;
    }

    /**
     * Convert DTO to Entity using Instance Method. This method creates a new
     * QuantityMeasurementEntity and copies all the relevant fields from this
     * DTO to the new entity.
     *
     * @return a QuantityMeasurementEntity representing the data from this DTO
     */
    public QuantityMeasurementEntity toEntity() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        entity.setThisValue(this.thisValue);
        entity.setThisUnit(this.thisUnit);
        entity.setThisMeasurementType(this.thisMeasurementType);
        entity.setThatValue(this.thatValue);
        entity.setThatUnit(this.thatUnit);
        entity.setThatMeasurementType(this.thatMeasurementType);
        entity.setOperation(this.operation);
        entity.setResultString(this.resultString);
        entity.setResultValue(this.resultValue);
        entity.setResultUnit(this.resultUnit);
        entity.setResultMeasurementType(this.resultMeasurementType);
        entity.setErrorMessage(this.errorMessage);
        entity.setError(this.error); 
        return entity;
    }

    /**
     * Convert List of Entities to List of DTOs. For this, we can use Java Streams to
     * map each entity in the list to a DTO using the `from` method defined above,
     * and then collect the results into a new list. This method provides a convenient
     * way to convert a list of entities retrieved from the database into a list of DTOs
     * that can be used in the service layer or returned by REST controllers.
     *
     * @param entities the list of QuantityMeasurementEntity objects to be converted to DTOs
     * @return a list of QuantityMeasurementDTO objects representing the data from the input
     * entities
     */
    public static List<QuantityMeasurementDTO> fromList(List<QuantityMeasurementEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(QuantityMeasurementDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Convert List of DTOs to List of Entities.
     * * @param dtos the list of QuantityMeasurementDTO objects to be converted to Entities
     * @return a list of QuantityMeasurementEntity objects
     */
    public static List<QuantityMeasurementEntity> toEntityList(List<QuantityMeasurementDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(QuantityMeasurementDTO::toEntity)
                .collect(Collectors.toList());
    }
}