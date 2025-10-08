package com.fci.sdk.exception;

import java.io.Serial;

/**
 * Exception thrown when the layer definition is missing or invalid in FCI operations.
 */
public class FciMissingLayerDefinitionException extends FciGenericException {

    @Serial
    private static final long serialVersionUID = 8577542572153995460L;

    /**
     * Constructs a new FciExceptionMissingLayerDefinition with the specified detail message.
     *
     * @param message the detail message
     */
    public FciMissingLayerDefinitionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FciExceptionMissingLayerDefinition with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public FciMissingLayerDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FciExceptionMissingLayerDefinition with the specified cause.
     *
     * @param cause the cause
     */
    public FciMissingLayerDefinitionException(final Throwable cause) {
        super(cause);
    }
} 
