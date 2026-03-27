package com.seveneleven.quantity_measurement_app.exception;

/**
 * Custom exception class for Quantity Measurement application.
 * This exception is thrown when there is a business logic error during
 * quantity comparison, conversion, or arithmetic operations.
 * * It extends RuntimeException so that it can be thrown without being
 * explicitly declared in method signatures, and it is caught by the
 * GlobalExceptionHandler to return a structured 400 Bad Request response.
 */

@SuppressWarnings("serial")
public class QuantityMeasurementException extends RuntimeException {

    /**
     * Constructs a new QuantityMeasurementException with the specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public QuantityMeasurementException(String message) {
        super(message);
    }

    /**
     * Constructs a new QuantityMeasurementException with the specified detail message 
     * and cause.
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public QuantityMeasurementException(String message, Throwable cause) {
        super(message, cause);
    }
}