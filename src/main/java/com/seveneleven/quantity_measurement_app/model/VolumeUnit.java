package com.seveneleven.quantity_measurement_app.model;

public enum VolumeUnit implements IMeasurableUnit {
    LITRE, MILLILITER, GALLON, CUBIC_METER, CUBIC_CENTIMETER, FLUID_OUNCE, PINT, QUART;

    @Override 
    public String getMeasurementType() { 
        return "VolumeUnit"; 
    }
}