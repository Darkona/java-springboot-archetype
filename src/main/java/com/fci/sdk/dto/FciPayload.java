package com.fci.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * FCI Payload DTO representing the complete structure of FCI events with
 * consolidated attributes.
 */
@Builder
@Setter
@Getter
public class FciPayload {

    /**
     * The FCI schema version (currently "v4").
     */
    public static final String VERSION_DEFAULT = "v4";

    /**
     * The FCI schema version (currently "v4").
     */
    @JsonProperty("version")
    private String version;

    /**
     * The contract type for this FCI event.
     */
    @JsonProperty("contract")
    private FciContractEnum contract;

    /**
     * Customer identity information.
     */
    @JsonProperty("identity")
    private FciIdentity identity;

    /**
     * Generic data storage for flexible key-value pairs.
     */
    @JsonProperty("data")
    private Map<String, String> data;

    /**
     * Event metadata, transaction information, and context details.
     */
    @JsonProperty("meta")
    private FciMetadata metadata;

    /**
     * Error information for failed interactions.
     */
    @JsonProperty("err")
    private FciError error;

    /**
     * Creates a new FciPayload with default values.
     */
    public FciPayload() {
        this.version = VERSION_DEFAULT;
        this.contract = FciContractEnum.FCI_PAYLOAD;
        this.data = new HashMap<>();
        this.metadata = new FciMetadata();
        this.error = new FciError();
    }

    /** Creates a new FciPayload with contact only
     *
     * @param contract The contract type
     */
    public FciPayload(final FciContractEnum contract) {
        this.version = VERSION_DEFAULT;
        this.contract = contract;
        this.data = new HashMap<>();
        this.metadata = FciMetadata.builder().timestamp(Instant.now().toString()).build();
        this.error = new FciError();
    }

    /**
     * Creates a new FciPayload with minimal information.
     *
     * @param actionName   The name of the action
     * @param artifactName The name of the service
     * @param channel      The interaction channel
     * @param flow         The business flow
     */
    public FciPayload(final String actionName, final String artifactName,
                      final String channel, final String flow) {
        this.version = VERSION_DEFAULT;
        this.contract = FciContractEnum.FCI_PAYLOAD;
        this.data = new HashMap<>();
        this.metadata = FciMetadata.builder().timestamp(Instant.now().toString())
                                   .actionName(actionName).artifactName(artifactName).channel(channel)
                                   .flow(flow).build();
    }

    /**
     * Creates a new FciPayload with default values.
     *
     * @param actionName   The name of the action
     * @param criticality  The criticality of the request
     * @param artifactName The name of the service
     * @param channel      The interaction channel
     * @param flow         The business flow
     * @param contract     The contract type
     */
    public FciPayload(final FciContractEnum contract, final String actionName, final String criticality,
                      final String artifactName, final String channel, final String flow) {
        this.version = VERSION_DEFAULT;
        this.contract = contract;
        this.data = new HashMap<>();
        this.metadata = FciMetadata.builder().timestamp(Instant.now().toString())
                                   .actionName(actionName).criticality(criticality)
                                   .artifactName(artifactName).channel(channel).flow(flow).build();
    }

    /**
     * Creates a new FciPayload with all attributes.
     *
     * @param version   The FCI schema version
     * @param contract  The contract type
     * @param identity  The customer identity information
     * @param data      Additional data as key-value pairs
     * @param metadata  Metadata for the event
     * @param error     Error information if any
     */

    public FciPayload(String version, FciContractEnum contract,
                      FciIdentity identity, Map<String, String> data, FciMetadata metadata,
                      FciError error) {
        this.version = version;
        this.contract = contract;
        this.identity = identity;
        this.data = data;
        this.metadata = metadata;
        this.error = error;
    }

    /**
     * Creates FciPayload for initialization events.
     *
     * @param actionName   The name of the action
     * @param artifactName The name of the microservice
     * @param channel      The interaction channel
     * @param flow         The business flow
     * @return FciPayload configured for fci_init
     */
    public static FciPayload forInit(final String actionName,
                                     final String artifactName, final String channel, final String flow) {
        final FciPayload payload = new FciPayload(actionName, artifactName, channel,
                flow);
        payload.setContract(FciContractEnum.FCI_INIT);
        return payload;
    }

    /**
     * Creates FciPayload for end events.
     *
     * @param actionName   The name of the action
     * @param artifactName The name of the microservice
     * @param channel      The interaction channel
     * @param flow         The business flow
     * @return FciPayload configured for fci_end
     */
    public static FciPayload forEnd(final String actionName,
                                    final String artifactName, final String channel, final String flow) {
        final FciPayload payload = new FciPayload(actionName, artifactName, channel,
                flow);
        payload.setContract(FciContractEnum.FCI_END);
        return payload;
    }

    /**
     * Sets identity information for the customer.
     *
     * @param userId   User identifier
     * @param email    User's email address
     * @param entityId Entity identifier
     * @param deviceId Device identifier
     */
    public final void setIdentity(final String userId, final String email,
                                  final String entityId, final String deviceId) {
        this.identity = FciIdentity.builder().userId(userId).email(email)
                                   .entityId(entityId).deviceId(deviceId).build();
    }

    /**
     * Sets error information for the payload.
     *
     * @param errorType        The error type
     * @param errorMessage     The error message
     * @param errorDescription Optional error description
     */
    public final void setError(final String errorType, final String errorMessage,
                               final String errorDescription) {
        this.error = FciError.builder().type(errorType).message(errorMessage)
                             .description(errorDescription).build();
    }

    /**
     * Sets error information from an exception.
     *
     * @param exception The exception to extract error information from
     */
    public final void setError(final Exception exception) {
        this.error = FciError.builder().type(exception.getClass().getSimpleName())
                             .message(exception.getMessage()).description(exception.toString())
                             .build();
    }

    /**
     * Identity information for the customer.
     */
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FciIdentity {

        /**
         * The user identifier.
         */
        @JsonProperty("user_id")
        private String userId;

        /**
         * The user's email address.
         */
        @JsonProperty("email")
        private String email;

        /**
         * The entity identifier.
         */
        @JsonProperty("entity_id")
        private String entityId;

        /**
         * The device identifier.
         */
        @JsonProperty("device_id")
        private String deviceId;
    }

    /**
     * Metadata information for the FCI event.
     */
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class FciMetadata {

        /**
         * The timestamp when the event occurred.
         */
        @JsonProperty("timestamp")
        @Setter(AccessLevel.NONE)
        private String timestamp = Instant.now().toString();

        /**
         * The name of the microservice/artifact.
         */
        @JsonProperty("artifact_name")
        private String artifactName;

        /**
         * The application name from baggage.
         */
        @JsonProperty("application_name")
        private String applicationName;

        /**
         * The client name from baggage.
         */
        @JsonProperty("client_name")
        private String clientName;
        /**
         * The name of the action being performed.
         */
        @JsonProperty("action_name")
        private String actionName;

        /**
         * The request criticality level.
         */
        @JsonProperty("criticality")
        private String criticality;

        /**
         * The current transaction identifier.
         */
        @JsonProperty("transaction_id")
        private String transactionId;

        /**
         * The parent transaction identifier.
         */
        @JsonProperty("parent_transaction_id")
        private String parentTransactionId;

        /**
         * The service layer (e.g., "bff", "tc").
         */
        @JsonProperty("layer")
        private String layer;

        /**
         * The interaction channel.
         */
        @JsonProperty("channel")
        private String channel;

        /**
         * The business flow.
         */
        @JsonProperty("flow")
        private String flow;

        /**
         * The FCI context for the request.
         */
        @JsonProperty("context")
        private String context;

        /**
         * The market identifier for the request.
         * This is an ISO 3166-1 alpha-2 country code (e.g., "UY")
         * Do not expect any validation other it being to alpha characters (which
         * might be any UTF-8
         */
        @JsonProperty("market")
        private String market;

        /**
         * Language identifier for the request.
         * Expect ISO 639-1 + "_" + ISO 3166-1 alpha-2 format (e.g., "es_UY").
         */
        @JsonProperty("language")
        private String language;

    }

    /**
     * Error information for failed interactions.
     */
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class FciError {

        /**
         * The error type or classification.
         */
        @JsonProperty("type")
        private String type;

        /**
         * The error message.
         */
        @JsonProperty("message")
        private String message;

        /**
         * The detailed error description.
         */
        @JsonProperty("description")
        private String description;
    }
}