package com.fci.sdk.exception;

import java.io.Serial;

/**
 * Exception thrown when the channel is missing or invalid in FCI operations.
 */
public class FciMissingChannelException extends FciGenericException {

    @Serial
    private static final long serialVersionUID = 8577542572153995462L;

    /**
     * Constructs a new FciExceptionMissingChannel with the specified detail message.
     *
     * @param message the detail message
     */
    public FciMissingChannelException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FciExceptionMissingChannel with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public FciMissingChannelException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FciExceptionMissingChannel with the specified cause.
     *
     * @param cause the cause
     */
    public FciMissingChannelException(final Throwable cause) {
        super(cause);
    }
}
