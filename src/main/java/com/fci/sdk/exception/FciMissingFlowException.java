package com.fci.sdk.exception;

import java.io.Serial;

/**
 * Exception thrown when the flow is missing or invalid in FCI operations.
 */
public class FciMissingFlowException extends FciGenericException {

    @Serial
    private static final long serialVersionUID = 8577542572153995461L;

    /**
     * Constructs a new FciExceptionMissingFlow with the specified detail message.
     *
     * @param message the detail message
     */
    public FciMissingFlowException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FciExceptionMissingFlow with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public FciMissingFlowException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FciExceptionMissingFlow with the specified cause.
     *
     * @param cause the cause
     */
    public FciMissingFlowException(final Throwable cause) {
        super(cause);
    }
}
