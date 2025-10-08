package com.fci.sdk.api.advice;

/**
 * Record class to hold FCI context information.
 * This class is used to pass FCI context between controllers and exception handlers.
 *
 * @param actionName the name of the action being performed
 * @param channel the interaction channel
 * @param flow the business flow
 * @param transactionId the transaction id
 * @param parentTransactionId the parent transaction id
 * @param criticality the criticality level
 * @param market the market identifier
 * @param deviceId the device identifier
 * @param userId the user identifier
 * @param applicationName the application name
 * @param clientName the client name
 * @param language the language code
 */
public record RequestContext(String actionName, String channel, String flow, String transactionId, String parentTransactionId,
                             String criticality, String market,
                             String deviceId, String userId,
                             String applicationName, String clientName,
                             String language) {}
