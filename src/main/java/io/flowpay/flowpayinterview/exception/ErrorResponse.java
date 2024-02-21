package io.flowpay.flowpayinterview.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Represents an error response payload for API requests.
 * This class encapsulates the error message and detailed information
 * about what went wrong during request processing.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    /**
     * General error message describing the nature of the error.
     */
    private final String message;

    /**
     * Detailed error messages providing more context about the error.
     * This can include validation errors or any other specifics.
     */
    private final List<String> details;
}
