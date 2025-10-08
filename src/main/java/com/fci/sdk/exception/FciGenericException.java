package com.fci.sdk.exception;


import java.io.Serial;

/**
 * Exception thrown when the artifact name is missing or invalid in FCI operations.
 */
public abstract class FciGenericException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 8577542572153995463L;

    /**
     * Constructs a new FciGenericException with the specified detail message.
     *
     * @param message the detail message
     */
    public FciGenericException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FciGenericException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public FciGenericException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FciGenericException with the specified cause.
     *
     * @param cause the cause
     */
    public FciGenericException(final Throwable cause) {
        super(cause);
    }
}
