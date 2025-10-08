package com.fci.sdk.skeleton.exception;

/**
 * Base Exception for security related topics.
 * May be instantiated for very generic use cases like exceptions on csrf configuration code.
 * It should be extended for more specific use cases.
 */
public class SkeletonSecurityException extends GenericSkeletonException {

    private static final long serialVersionUID = 8577542572153995430L;
    
    /**
     * Constructs a new SkeletonSecurityException with the specified detail message.
     *
     * @param message the detail message
     */
    public SkeletonSecurityException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SkeletonSecurityException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public SkeletonSecurityException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SkeletonSecurityException with the specified cause.
     *
     * @param cause the cause
     */
    public SkeletonSecurityException(final Throwable cause) {
        super(cause);
    }
}
