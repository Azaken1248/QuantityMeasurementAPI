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

    @JsonProperty("error")
    public boolean error;

    public ComparisonResult comparisonResult;

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
        dto.setComparisonResult(entity.getComparisonResult());
        return dto;
    }

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
        entity.setComparisonResult(this.comparisonResult);
        return entity;
    }

    public static List<QuantityMeasurementDTO> fromList(List<QuantityMeasurementEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(QuantityMeasurementDTO::from)
                .collect(Collectors.toList());
    }

    public static List<QuantityMeasurementEntity> toEntityList(List<QuantityMeasurementDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(QuantityMeasurementDTO::toEntity)
                .collect(Collectors.toList());
    }
}
