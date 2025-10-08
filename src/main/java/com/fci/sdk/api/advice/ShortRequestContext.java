package com.fci.sdk.api.advice;

import lombok.Builder;

/**
 * Record class to hold FCI context information.
 * This class is used to pass FCI context between controllers and exception handlers.
 *
 * @param actionName the name of the action being performed
 * @param transactionId the transaction id
 * @param parentTransactionId the parent transaction id
 * @param criticality the criticality level
 */
@Builder
public record ShortRequestContext(String actionName,
                                  String transactionId,
                                  String parentTransactionId, String criticality) {


}


