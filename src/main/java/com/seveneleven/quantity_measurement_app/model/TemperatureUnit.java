package com.seveneleven.quantity_measurement_app.model;

public enum TemperatureUnit implements IMeasurableUnit {
    CELSIUS, FAHRENHEIT;

    @Override 
    public String getMeasurementType() { 
        return "TemperatureUnit"; 
    }
}