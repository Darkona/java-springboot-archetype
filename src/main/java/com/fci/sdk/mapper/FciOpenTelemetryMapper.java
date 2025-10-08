package com.fci.sdk.mapper;

import com.fci.sdk.constant.FciContractEnum;
import com.fci.sdk.dto.FciPayload;
import com.fci.sdk.skeleton.request.SkeletonBaggageConstants;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.context.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

//TODO: Introduce an interface for FciOpenTelemetryMapper and FciRequestAttributesMapper
// At leas make them similar

/**
 * Mapper for converting OpenTelemetry Context to FciPayload.
 * Uses the current thread context and baggage information to populate FCI events.
 */
@Component
public class FciOpenTelemetryMapper {

    /** Application artifact name configured from properties. */
    @Value("${spring.application.name:Skeleton}")
    private String artifactName;

    /**
     * Creates an FciPayload from the current OpenTelemetry context.
     * 
     * @param contract The contract type for the FCI event
     * @return FciPayload populated from OpenTelemetry context
     */
    public FciPayload fromCurrentContext(final FciContractEnum contract) {
        final Context currentContext = Context.current();
        final Baggage baggage = Baggage.fromContext(currentContext);
        
        return fromBaggage(baggage, contract);
    }
    
    /**
     * Creates an FciPayload from OpenTelemetry baggage.
     * 
     * @param baggage The OpenTelemetry baggage
     * @param contract The contract type for the FCI event
     * @return FciPayload populated from baggage
     */
    public FciPayload fromBaggage(final Baggage baggage, final FciContractEnum contract) {
        final FciPayload payload = new FciPayload();
        
        // Set top-level fields
        payload.setVersion("v4");
        payload.setContract(contract);
        
        // Set identity from baggage
        final String userId = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_USER_ID);
        final String deviceId = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_DEVICE_ID);
        final String applicationName = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_APPLICATION_NAME);
        
        if (userId != null || deviceId != null || applicationName != null) {
            payload.setIdentity(
                userId,
                null, // email not in baggage
                applicationName,
                deviceId
            );
        }
        
        // Set metadata from baggage
        final FciPayload.FciMetadata metadata = FciPayload.FciMetadata.builder()
                                                                      .timestamp(Instant.now().toString())
                                                                      .transactionId(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_TRANSACTION_ID))
                                                                      .parentTransactionId(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_PARENT_TRANSACTION_ID))
                                                                      .layer(null) // not in baggage, must be set separately
                                                                      .actionName(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_ACTION))
                                                                      .applicationName(applicationName)
                                                                      .artifactName(artifactName)
                                                                      .channel(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CHANNEL))
                                                                      .flow(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_FLOW))
                                                                      .criticality(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CRITICALITY))
                                                                      //TODO: Map from set attributes
                                                                      .context(null) // not in baggage, must be set via request attributes
                                                                      .build();
        
        payload.setMetadata(metadata);
        
        // Set data from baggage
        final Map<String, String> data = new HashMap<>();
        data.put("market", baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_MARKET));
        data.put("language", baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_LANGUAGE));
        data.put("client_name", baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CLIENT_NAME));
        
        // Remove null values
        data.entrySet().removeIf(entry -> entry.getValue() == null);
        payload.setData(data);
        
        return payload;
    }
    
    /**
     * Creates an FciPayload for initialization events from current context.
     * 
     * @return FciPayload configured for fci_init
     */
    public final FciPayload forInit() {
        return fromCurrentContext(FciContractEnum.FCI_INIT);
    }
    
    /**
     * Creates an FciPayload for end events from current context.
     * 
     * @return FciPayload configured for fci_end
     */
    public final FciPayload forEnd() {
        return fromCurrentContext(FciContractEnum.FCI_END);
    }
    
    /**
     * Creates an FciPayload for payload events from current context.
     * 
     * @return FciPayload configured for fci_payload
     */
    public final FciPayload forPayload() {
        return fromCurrentContext(FciContractEnum.FCI_PAYLOAD);
    }
}
