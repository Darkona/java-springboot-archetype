package com.fci.sdk.exception;

import java.io.Serial;

/**
 * Exception thrown when synchronous FCI operations fail while sending payloads.
 * 
 * This exception is used to signal failures in synchronous FCI flows where
 * the operation cannot complete successfully. It extends FciGenericException
 * to maintain consistency with the FCI exception hierarchy.
 * 
 * <p>Note: The default FCI send operations use CompletableFuture and do not
 * throw exceptions. This exception is specifically for synchronous operations
 * that need to signal immediate failures.</p>
 * 
 * <p>Usage examples:</p>
 * <ul>
 *   <li>Synchronous payload validation failures</li>
 *   <li>Immediate service unavailability</li>
 *   <li>Configuration errors that prevent sending</li>
 *   <li>Business logic violations that require immediate failure</li>
 * </ul>
 */
public class FciSendException extends FciGenericException {

    @Serial
    private static final long serialVersionUID = 8577542572153995432L;
    
    /**
     * Constructs a new FciSendException with the specified detail message.
     *
     * @param message the detail message
     */
    public FciSendException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FciSendException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public FciSendException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FciSendException with the specified cause.
     *
     * @param cause the cause
     */
    public FciSendException(final Throwable cause) {
        super(cause);
    }
}
