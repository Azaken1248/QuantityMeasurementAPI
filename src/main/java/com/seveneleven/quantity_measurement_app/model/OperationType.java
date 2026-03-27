package com.seveneleven.quantity_measurement_app.model;

public enum OperationType {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    COMPARE,
    CONVERT;

    // Optional: Add display names
    public String getDisplayName() {
        return this.name().toLowerCase();
    }
}