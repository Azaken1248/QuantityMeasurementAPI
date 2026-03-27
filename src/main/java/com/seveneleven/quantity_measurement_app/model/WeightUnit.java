package com.seveneleven.quantity_measurement_app.model;

public enum WeightUnit implements IMeasurableUnit {
    MILLIGRAM, GRAM, KILOGRAM, POUND, TONNE;

    @Override 
    public String getMeasurementType() { 
        return "WeightUnit"; 
    }
}