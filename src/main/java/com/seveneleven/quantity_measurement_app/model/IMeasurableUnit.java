package com.seveneleven.quantity_measurement_app.model;


/**
 * Interface to represent measurable units for quantity measurements in the API layer.
 */
public interface IMeasurableUnit {
    String name();
    String getMeasurementType();
}
