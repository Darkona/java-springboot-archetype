package com.fci.sdk.exception;

import java.io.Serial;

/**
 * Exception thrown when the artifact name is missing or invalid in FCI operations.
 */
public class FciMissingArtifactNameException extends FciGenericException {

    @Serial
    private static final long serialVersionUID = 8577542572153995463L;

    /**
     * Constructs a new FciExceptionMissingArtifactName with the specified detail message.
     *
     * @param message the detail message
     */
    public FciMissingArtifactNameException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FciExceptionMissingArtifactName with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public FciMissingArtifactNameException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FciExceptionMissingArtifactName with the specified cause.
     *
     * @param cause the cause
     */
    public FciMissingArtifactNameException(final Throwable cause) {
        super(cause);
    }
} 
