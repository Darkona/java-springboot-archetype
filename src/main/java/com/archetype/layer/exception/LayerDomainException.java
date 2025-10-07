package com.archetype.layer.exception;

/**
 * Base exception class for all domain-specific exceptions in the layer module.
 * Follows ADR 0003 (Domain validation and robustness) and ADR 0016 (Exception handling strategy).
 * <p>
 * All domain exceptions should extend this class and provide:
 * - Error codes for internationalization
 * - Message arguments for parameterized messages
 * - Proper logging context
 */
public abstract class LayerDomainException extends RuntimeException {

    private final String errorCode;
    private final Object[] messageArgs;

    /**
     * Create a domain exception with error code and message arguments.
     *
     * @param errorCode   The message key for internationalization (e.g., "pokemon.not-found")
     * @param messageArgs Optional arguments for parameterized messages
     */
    protected LayerDomainException(String errorCode, Object... messageArgs) {
        super(errorCode); // Temporary message, will be replaced by localized version
        this.errorCode = errorCode;
        this.messageArgs = messageArgs != null ? messageArgs.clone() : new Object[0];
    }

    /**
     * Create a domain exception with error code, message arguments, and underlying cause.
     *
     * @param errorCode   The message key for internationalization
     * @param cause       The underlying cause of this exception
     * @param messageArgs Optional arguments for parameterized messages
     */
    protected LayerDomainException(String errorCode, Throwable cause, Object... messageArgs) {
        super(errorCode, cause);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs != null ? messageArgs.clone() : new Object[0];
    }

    /**
     * Get the error code for message resolution.
     *
     * @return The error code key for internationalization
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get the message arguments for parameterized messages.
     *
     * @return Array of message arguments (defensive copy)
     */
    public Object[] getMessageArgs() {
        return messageArgs.clone();
    }

    /**
     * Get the error reason code for API responses.
     * Default implementation derives from error code.
     *
     * @return Machine-readable reason code
     */
    public String getReasonCode() {
        return errorCode.toUpperCase().replace('.', '_').replace('-', '_');
    }
}
