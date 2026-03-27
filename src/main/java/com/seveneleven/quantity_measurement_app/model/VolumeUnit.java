package com.seveneleven.quantity_measurement_app.model;

public enum VolumeUnit implements IMeasurableUnit {
    LITRE, MILLILITER, GALLON;

    @Override 
    public String getMeasurementType() { 
        return "VolumeUnit"; 
    }
}