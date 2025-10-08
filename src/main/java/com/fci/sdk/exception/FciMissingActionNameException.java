package com.fci.sdk.exception;

import java.io.Serial;

/**
 * Exception thrown when the action name is missing or invalid in FCI operations.
 */
public class FciMissingActionNameException extends FciGenericException {

    @Serial
    private static final long serialVersionUID = 8577542572153995464L;
    /**
     * Constructs a new FciExceptionMissingActionName with the specified detail message.
     *
     * @param message the detail message
     */
    public FciMissingActionNameException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FciExceptionMissingActionName with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public FciMissingActionNameException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FciExceptionMissingActionName with the specified cause.
     *
     * @param cause the cause
     */
    public FciMissingActionNameException(final Throwable cause) {
        super(cause);
    }
}
