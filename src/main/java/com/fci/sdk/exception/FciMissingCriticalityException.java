package com.fci.sdk.exception;

import java.io.Serial;

/**
 * Exception thrown when the criticality is missing or invalid in FCI operations.
 */
public class FciMissingCriticalityException extends FciGenericException {

    @Serial
    private static final long serialVersionUID = 8577542572153995459L;

    /**
     * Constructs a new FciMissingCriticalityException with the specified detail message.
     *
     * @param message the detail message
     */
    public FciMissingCriticalityException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FciMissingCriticalityException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public FciMissingCriticalityException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FciMissingCriticalityException with the specified cause.
     *
     * @param cause the cause
     */
    public FciMissingCriticalityException(final Throwable cause) {
        super(cause);
    }
}
