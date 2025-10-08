package com.fci.sdk.skeleton.exception;

import java.io.Serial;

/**
 * Base Exception for configuration related topics.
 * May be instantiated for very generic use cases like missing required properties
 * in application.yaml files or other configuration issues.
 * It should be extended for more specific use cases.
 */
public class SkeletonConfigurationException extends GenericSkeletonException {

    @Serial
    private static final long serialVersionUID = 8577542572153995431L;
    
    /**
     * Constructs a new SkeletonConfigurationException with the specified detail message.
     *
     * @param message the detail message
     */
    public SkeletonConfigurationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SkeletonConfigurationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public SkeletonConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SkeletonConfigurationException with the specified cause.
     *
     * @param cause the cause
     */
    public SkeletonConfigurationException(final Throwable cause) {
        super(cause);
    }
}
