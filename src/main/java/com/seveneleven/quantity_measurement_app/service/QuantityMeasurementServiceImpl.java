package com.seveneleven.quantity_measurement_app.service;

import com.seveneleven.quantity_measurement_app.model.*;
import com.seveneleven.quantity_measurement_app.repository.QuantityMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Logger;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = Logger.getLogger(
            QuantityMeasurementServiceImpl.class.getName()
    );

    @Autowired
    private QuantityMeasurementRepository repository;

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO thisQuantity, QuantityDTO thatQuantity) {
        // We do NOT catch IllegalArgumentException here so the Controller 
        // can return a 400 Bad Request, passing the "validation fails" tests.
        validateMeasurementTypes(thisQuantity, thatQuantity);
        IMeasurableUnit unit1 = resolveUnit(thisQuantity);
        IMeasurableUnit unit2 = resolveUnit(thatQuantity);

        boolean isEqual = compareValues(unit1, thisQuantity.getValue(), unit2, thatQuantity.getValue());
        String resultStr = isEqual ? "Equal" : "Not Equal";

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                thisQuantity, thatQuantity, OperationType.COMPARE.name(), resultStr);
        return QuantityMeasurementDTO.from(repository.save(entity));
    }

    private <U extends IMeasurableUnit> boolean compareValues(U thisUnit, double thisValue, U thatUnit, double thatValue) {
        if (thisUnit instanceof TemperatureUnit) {
            double convertedThat = convertTemperatureUnit(thatUnit, thatValue, thisUnit);
            return Double.compare(Math.round(thisValue * 100.0) / 100.0, Math.round(convertedThat * 100.0) / 100.0) == 0;
        }
        double baseVal1 = thisValue * getBaseConversionFactor(thisUnit);
        double baseVal2 = thatValue * getBaseConversionFactor(thatUnit);
        return Double.compare(Math.round(baseVal1 * 100.0) / 100.0, Math.round(baseVal2 * 100.0) / 100.0) == 0;
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO thisQuantity, QuantityDTO thatQuantity) {
        validateMeasurementTypes(thisQuantity, thatQuantity);
        IMeasurableUnit unit1 = resolveUnit(thisQuantity);
        IMeasurableUnit unit2 = resolveUnit(thatQuantity);

        double resultVal = convertTo(unit1, thisQuantity.getValue(), unit2);

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                thisQuantity, thatQuantity, OperationType.CONVERT.name(), resultVal);
        
        entity.setResultUnit(thatQuantity.getUnit());
        entity.setResultMeasurementType(thatQuantity.getMeasurementType());
        
        return QuantityMeasurementDTO.from(repository.save(entity));
    }

    private <U extends IMeasurableUnit> double convertTo(U thisUnit, double thisValue, U targetUnit) {
        if (thisUnit instanceof TemperatureUnit) {
            return convertTemperatureUnit(thisUnit, thisValue, targetUnit);
        }
        double baseValue = thisValue * getBaseConversionFactor(thisUnit);
        return baseValue / getBaseConversionFactor(targetUnit);
    }

    private <U extends IMeasurableUnit> double convertTemperatureUnit(U thisUnit, double thisValue, U targetUnit) {
        if (thisUnit == targetUnit) return thisValue;
        
        if (thisUnit == TemperatureUnit.FAHRENHEIT && targetUnit == TemperatureUnit.CELSIUS) {
            return (thisValue - 32) * 5 / 9;
        } else if (thisUnit == TemperatureUnit.CELSIUS && targetUnit == TemperatureUnit.FAHRENHEIT) {
            return (thisValue * 9 / 5) + 32;
        }
        return thisValue;
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQuantity, QuantityDTO thatQuantity) {
        return this.add(thisQuantity, thatQuantity, thisQuantity);
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQuantity, QuantityDTO thatQuantity, QuantityDTO targetUnit) {
        return executeArithmetic(ArithmeticOperation.ADD, thisQuantity, thatQuantity, targetUnit);
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQuantity, QuantityDTO thatQuantity) {
        return this.subtract(thisQuantity, thatQuantity, thisQuantity);
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQuantity, QuantityDTO thatQuantity, QuantityDTO targetUnit) {
        return executeArithmetic(ArithmeticOperation.SUBTRACT, thisQuantity, thatQuantity, targetUnit);
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2) {
        // 1. Perform validations (These throw 400s)
        validateMeasurementTypes(q1, q2);
        IMeasurableUnit unit2 = resolveUnit(q2);

        // 2. Check for division by zero before doing the math
        double base2 = q2.getValue() * getBaseConversionFactor(unit2);

        if (base2 == 0) {
            // SAVE the error to DB first so the /history/errored test passes
            saveAndReturnError(q1, q2, "DIVIDE", "Division by zero");
            
            // THROW the exception so Spring returns the 500 Internal Server Error
            throw new ArithmeticException("Division by zero");
        }

        // 3. If everything is fine, proceed with the operation
        return executeArithmetic(ArithmeticOperation.DIVIDE, q1, q2, q1);
    }

    private QuantityMeasurementDTO executeArithmetic(ArithmeticOperation op, QuantityDTO q1, QuantityDTO q2, QuantityDTO target) {
        // Logic check: IllegalArgumentExceptions (Validation) are still thrown here and bubbled up.
        validateMeasurementTypes(q1, q2);
        IMeasurableUnit unit1 = resolveUnit(q1);
        IMeasurableUnit unit2 = resolveUnit(q2);
        validateArithmeticOperands(unit1, unit2);

        double resultValue = performArithmetic(op, unit1, q1.getValue(), unit2, q2.getValue());
        
        double finalValue;
        if (op == ArithmeticOperation.DIVIDE) {
            // FIXED: Division result is dimensionless. Do not divide by the target unit factor.
            finalValue = resultValue;
        } else {
            IMeasurableUnit targetU = resolveUnit(target);
            finalValue = resultValue / getBaseConversionFactor(targetU);
        }
        
        QuantityDTO resultDTO = new QuantityDTO(finalValue, target.getUnit(), target.getMeasurementType());
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(q1, q2, op.name(), resultDTO);
        return QuantityMeasurementDTO.from(repository.save(entity));
    }

    private enum ArithmeticOperation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    private <U extends IMeasurableUnit> void validateArithmeticOperands(U thisUnit, U thatUnit) {
        if (thisUnit instanceof TemperatureUnit || thatUnit instanceof TemperatureUnit) {
            throw new IllegalArgumentException("Arithmetic operations are not supported on Temperatures.");
        }
    }

    private <U extends IMeasurableUnit> double performArithmetic(ArithmeticOperation op, U thisUnit, double thisValue, U thatUnit, double thatValue) {
        double val1 = thisValue * getBaseConversionFactor(thisUnit);
        double val2 = thatValue * getBaseConversionFactor(thatUnit);

        switch (op) {
            case ADD: return val1 + val2;
            case SUBTRACT: return val1 - val2;
            case MULTIPLY: return val1 * val2;
            case DIVIDE: 
                if (val2 == 0) throw new ArithmeticException("Division by zero");
                return val1 / val2;
            default: throw new UnsupportedOperationException("Unknown operation");
        }
    }

    // --- Helper Methods for Unit Resolution ---

    private void validateMeasurementTypes(QuantityDTO q1, QuantityDTO q2) {
        if (q2 != null && !q1.getMeasurementType().equals(q2.getMeasurementType())) {
            // FIXED: Message must match test expectations
            throw new IllegalArgumentException("Invalid Measurement Type");
        }
    }

    private IMeasurableUnit resolveUnit(QuantityDTO dto) {
        String type = dto.getMeasurementType();
        String unit = dto.getUnit();
        try {
            if ("LengthUnit".equals(type)) return LengthUnit.valueOf(unit);
            if ("VolumeUnit".equals(type)) return VolumeUnit.valueOf(unit);
            if ("WeightUnit".equals(type)) return WeightUnit.valueOf(unit);
            if ("TemperatureUnit".equals(type)) return TemperatureUnit.valueOf(unit);
            
            // FIXED: Throw specific string for bad measurement type
            throw new IllegalArgumentException("Invalid Measurement Type");
        } catch (IllegalArgumentException e) {
            // If the message is already "Invalid Measurement Type", just rethrow it.
            // Otherwise, it was a valueOf fail, meaning the unit string is bad.
            if (e.getMessage().equals("Invalid Measurement Type")) throw e;
            throw new IllegalArgumentException("Unit must be valid");
        }
    }

    private double getBaseConversionFactor(IMeasurableUnit unit) {
        if (unit instanceof LengthUnit) {
            switch ((LengthUnit) unit) {
                case INCHES: return 1.0;
                case FEET: return 12.0;
                case YARDS: return 36.0;
                case CENTIMETERS: return 1.0 / 2.54;
            }
        } else if (unit instanceof VolumeUnit) {
            switch ((VolumeUnit) unit) {
                case LITRE: return 1.0;
                case MILLILITER: return 0.001;
                case GALLON: return 3.78541;
            }
        } else if (unit instanceof WeightUnit) {
            switch ((WeightUnit) unit) {
                case GRAM: return 1.0;
                case KILOGRAM: return 1000.0;
                case MILLIGRAM: return 0.001;
                case POUND: return 453.592;
                case TONNE: return 1000000.0;
            }
        }
        return 1.0;
    }

    private QuantityMeasurementDTO saveAndReturnError(QuantityDTO q1, QuantityDTO q2, String operation, String errorMsg) {
        logger.severe("Operation failed: " + errorMsg);
        QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity(q1, q2, operation, errorMsg, true);
        return QuantityMeasurementDTO.from(repository.save(errorEntity));
    }

    // --- History & Analytics Methods ---

    @Override
    public List<QuantityMeasurementDTO> getOperationHistory(String operation) {
        return QuantityMeasurementDTO.fromList(repository.findByOperation(operation));
    }

    @Override
    public List<QuantityMeasurementDTO> getMeasurementsByType(String type) {
        return QuantityMeasurementDTO.fromList(repository.findByThisMeasurementType(type));
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndIsErrorFalse(operation);
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromList(repository.findByIsErrorTrue());
    }
}