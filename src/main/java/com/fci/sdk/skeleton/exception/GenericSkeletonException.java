package com.fci.sdk.skeleton.exception;

/**
 * Generic exception class for the Skeleton SDK.
 * All skeleton-specific exceptions should inherit from this class.
 */
public abstract class GenericSkeletonException extends RuntimeException {

    private static final long serialVersionUID = 8577542572153995450L;

    /**
     * Constructs a new GenericSkeletonException with the specified detail message.
     *
     * @param message the detail message
     */
    protected GenericSkeletonException(final String message) {
        super(message);
    }

    /**
     * Constructs a new GenericSkeletonException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    protected GenericSkeletonException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new GenericSkeletonException with the specified cause.
     *
     * @param cause the cause
     */
    protected GenericSkeletonException(final Throwable cause) {
        super(cause);
    }
} 
