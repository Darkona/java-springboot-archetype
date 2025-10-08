package com.fci.sdk.skeleton.exception;

/**
 * Exception used for all validations and mapped to 400 by SkeletonExceptionHandler.
 * <p>
 * Can be reused in user code for validation purposes or extended for more specific use cases.
 * It should not be try/catched in user code, as it is handled by SkeletonExceptionHandler.
 */
public class SkeletonRequestValidationException extends GenericSkeletonException {

    private static final long serialVersionUID = 8577642572153995430L;

    /**
     * Constructs a new SkeletonRequestValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public SkeletonRequestValidationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SkeletonRequestValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public SkeletonRequestValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SkeletonRequestValidationException with the specified cause.
     *
     * @param cause the cause
     */
    public SkeletonRequestValidationException(final Throwable cause) {
        super(cause);
    }
}
