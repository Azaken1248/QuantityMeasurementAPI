package com.seveneleven.quantity_measurement_app.model;

public enum LengthUnit implements IMeasurableUnit {
    FEET, INCHES, YARDS, CENTIMETERS ,METERS, KILOMETERS, MILLIMETERS, MILES;

    @Override 
    public String getMeasurementType() { 
        return "LengthUnit"; 
    }
}