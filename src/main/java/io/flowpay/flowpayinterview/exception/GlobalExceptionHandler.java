package io.flowpay.flowpayinterview.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides global exception handling for the application.
 * This class centralizes the handling of common exceptions thrown by controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles not found exceptions thrown by the application.
     *
     * @param ex The caught EntityNotFoundException.
     * @param request Details about the web request that resulted in the exception.
     * @return A ResponseEntity containing the ErrorResponse and HTTP status code.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), List.of(request.getDescription(false)));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles generic exceptions not specifically addressed by other exception handlers.
     *
     * @param ex The caught generic Exception.
     * @param request Details about the web request that resulted in the exception.
     * @return A ResponseEntity containing the ErrorResponse and HTTP status code.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", List.of(request.getDescription(false)));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation failures for method arguments annotated with @Valid.
     *
     * @param ex The caught MethodArgumentNotValidException.
     * @param request Details about the web request that resulted in the exception.
     * @return A ResponseEntity containing the validation error details and HTTP status code.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse("Validation failed", details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles database integrity violations, such as unique constraint violations.
     *
     * @param ex The caught DataIntegrityViolationException.
     * @param request Details about the web request that resulted in the exception.
     * @return A ResponseEntity containing the ErrorResponse and HTTP status code.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String detailMessage = extractDetailMessageFromException(ex);
        ErrorResponse errorResponse = new ErrorResponse("Data integrity violation", List.of(detailMessage));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    private String extractDetailMessageFromException(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();

        if (errorMessage.contains("constraint [\"UK_")) {
            return "A unique constraint violation occurred.";
        } else if (errorMessage.contains("foreign key constraint fails")) {
            return "A foreign key constraint violation occurred.";
        } else {
            return "An unspecified data integrity violation occurred.";
        }
    }
}
