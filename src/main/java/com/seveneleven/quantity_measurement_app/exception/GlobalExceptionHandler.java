package com.seveneleven.quantity_measurement_app.exception;

import java.util.List;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * GlobalExceptionHandler is a centralized exception handling class for the Quantity
 * Measurement application. It uses Spring's @ControllerAdvice annotation to intercept
 * exceptions thrown by any controller and provides custom responses for specific
 * exceptions like QuantityMeasurementException as well as a generic handler for all
 * other exceptions. The class defines three main methods:
 * - handleMethodArgumentNotValidException: Handles validation errors that occur when
 * the input data fails to meet the specified constraints. It extracts the error
 * messages from the exception and constructs a structured error response with details
 * about the validation failure.
 * - handleQuantityException: Handles exceptions of type QuantityMeasurementException
 * and returns a structured error response with details about the error, including a
 * timestamp, HTTP status code, error type, message, and the request path that caused
 * the error.
 * - handleGlobalException: A catch-all handler for any other exceptions that may occur,
 * providing a similar structured error response but with a generic "Internal Server
 * Error" message.
 *
 * The ErrorResponse class is a simple POJO that encapsulates the details of the error
 * response sent back to the client. This approach ensures that clients receive
 * consistent and informative error messages, improving the overall robustness and
 * user experience of the API.
 */
class ErrorResponse {
    public LocalDateTime timestamp;
    public int status;
    public String error;
    public String message;
    public String path;
}

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(
            GlobalExceptionHandler.class.getName()
    );

    /**
     * Handle MethodArgumentNotValidException which occurs when validation on an
     * argument annotated with @Valid fails.
     * @param ex the MethodArgumentNotValidException thrown when validation fails
     * @return a ResponseEntity containing an ErrorResponse with details about the
     * validation error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request
    ) {
        logger.warning(String.format("The Exception is %s", ex.getMessage()));

        List<ObjectError> errorList = ex.getBindingResult().getAllErrors();
        List<String> errMesg = errorList.stream()
                .map(objErr -> objErr.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse();
        error.timestamp = LocalDateTime.now();
        error.status = HttpStatus.BAD_REQUEST.value();
        error.error = "Quantity Measurement Error";
        error.message = String.join("; ", errMesg);
        error.path = request.getDescription(false).replace("uri=", "");

        logger.warning("Handling QuantityMeasurementException: " + ex.getMessage() +
                " for request path: " + error.path);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle QuantityMeasurementException which is a custom exception for errors related to
     * quantity measurement operations. This method constructs an ErrorResponse with details
     * about the error and returns it with a BAD_REQUEST status.
     * @param ex the QuantityMeasurementException thrown during quantity measurement
     * operations
     * @param request the WebRequest that resulted in the exception, used to extract the
     * request path for logging
     * @return a ResponseEntity containing an ErrorResponse with details about the error
     */
    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<ErrorResponse> handleQuantityException(
            QuantityMeasurementException ex,
            WebRequest request
    ) {
        ErrorResponse error = new ErrorResponse();
        error.timestamp = LocalDateTime.now();
        error.status = HttpStatus.BAD_REQUEST.value();
        error.error = "Quantity Measurement Error";
        error.message = ex.getMessage();
        error.path = request.getDescription(false).replace("uri=", "");

        logger.warning("Handling QuantityMeasurementException: " + ex.getMessage() +
                " for request path: " + error.path);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle any other exceptions that are not specifically handled by other methods.
     * This method constructs a generic ErrorResponse with details about the error and
     * returns it with an INTERNAL_SERVER_ERROR status to indicate that an unexpected
     * error occurred.
     * @param ex the Exception that was thrown
     * @param request the WebRequest that resulted in the exception, used to extract the
     * request path for logging
     * @return a ResponseEntity containing an ErrorResponse with details about the error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request
    ) {
        ErrorResponse error = new ErrorResponse();
        error.timestamp = LocalDateTime.now();
        error.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        error.error = "Internal Server Error";
        error.message = ex.getMessage();
        error.path = request.getDescription(false).replace("uri=", "");

        logger.severe("Handling global exception: " + ex.getMessage() +
                " for request path: " + error.path);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}