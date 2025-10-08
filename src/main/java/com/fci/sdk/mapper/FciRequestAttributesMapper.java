package com.fci.sdk.mapper;

import com.dummy.sdk.fci.constant.FciContextConstants;
import com.dummy.sdk.fci.constant.FciContractEnum;
import com.dummy.sdk.fci.dto.FciPayload;
import com.dummy.sdk.fci.dto.FciPayload.FciIdentity;
import com.dummy.sdk.skeleton.request.SkeletonBaggageConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * Mapper for converting ServletRequestAttributes to FciPayload.
 * 
 * This mapper extracts FCI context information from request attributes that were set by 
 * the SkeletonBaggageToAttributesInterceptor. It maps OpenTelemetry baggage data to the 
 * appropriate FciPayload fields.
 * 
 * <p>The mapper handles the following mappings:</p>
 * <ul>
 *   <li><strong>Identity:</strong> userId and deviceId from request attributes</li>
 *   <li><strong>Metadata:</strong> All metadata fields including transaction IDs, action names, 
 *       channel, flow, criticality, context, client name, market, and language</li>
 *   <li><strong>Data:</strong> Copies input data map to ensure immutability</li>
 * </ul>
 * 
 * <p>Note: email and entityId are not mapped from request attributes and must be set 
 * separately using the FciPayload identity setters.</p>
 */
@Component
public class FciRequestAttributesMapper {

    /** 
     * Application artifact name configured from properties.
     * Defaults to "Skeleton" if not configured.
     */
    @Value("${spring.application.name}")
    private String artifactName;

    /**
     * Default service layer for FCI events.
     * Configured via the "fci.default.layer" property.
     */
    @Value("${fci.default.layer}")
    private String layer;

    /**
     * Creates an FciPayload from ServletRequestAttributes.
     * 
     * @param attr The ServletRequestAttributes containing baggage attributes
     * @param contract The contract type for the FCI event
     * @param data Additional data to include in the payload (can be null)
     * @return FciPayload populated from request attributes
     * <p>
     * data is copied into a new Map to ensure immutability.
     * <p>
     * FciPayload
     * ├── version: String
     * ├── contract: FciContractEnum
     * ├── identity: FciIdentity
     * │   ├── userId: String
     * │   ├── deviceId: String
     * │   ├── email: String
     * │   └── entityId: String
     * ├── metadata: FciMetadata
     * │   ├── timestamp: String
     * │   ├── transactionId: String
     * │   ├── parentTransactionId: String
     * │   ├── layer: String
     * │   ├── actionName: String
     * │   ├── applicationName: String
     * │   ├── artifactName: String
     * │   ├── channel: String
     * │   ├── flow: String
     * │   ├── criticality: String
     * │   ├── context: String
     * │   ├── clientName: String
     * │   ├── market: String
     * │   └── language: String
     * ├── error: FciError #This is always null after this mapper
     * │   ├── type: String
     * │   ├── message: String
     * │   └── description: String
     * └── data: Map<String, String> #Copied from input data parameter
     */
    public final FciPayload toFciPayload(final ServletRequestAttributes attr,
        final FciContractEnum contract, final Map<String, String> data) {
      final FciPayload payload = new FciPayload(contract);

      /* Map identity data
       * userId: from request attribute "skel:u_id"
       * userId: from request attribute "skel:u_id"
       * deviceId: from request attribute "skel:d_id"
       * email: not mapped, must be set using FciPayload.getIdentity().setEmail()
       * entityId: not mapped, must be set using FciPayload.getIdentity().setEntityId()
       */
      final String userId = getAttributeValue(attr,
          SkeletonBaggageConstants.RQ_ATTR_USER_ID);
      final String deviceId = getAttributeValue(attr,
          SkeletonBaggageConstants.RQ_ATTR_DEVICE_ID);

      // TODO: We could manage this in a wey we do not create an identity
      // object if there's no userId or deviceId present in attributes
      payload.setIdentity(
          FciIdentity.builder().userId(userId).deviceId(deviceId).build());

      /* Map metadata
       * timestamp: current time in ISO 8601 format
       * artifactName: from application property "spring.application.name" or default "Skeleton"
       * applicationName: from request attribute "skel:app"
       * clientName: from request attribute "skel:client"
       * channel: from request attribute "skel:channel"
       * actionName: from request attribute "skel:action"
       * flow: from request attribute "skel:flow"
       * criticality: from request attribute "skel:crit"
       * transactionId: from request attribute "skel:t9n_id"
       * parentTransactionId: from request attribute "skel:parent_t9n_id"
       * layer: from application property "fci.default.layer"
       * context: from request attribute "fci:context"
       */

      //TODO: Reorder this to make sense
      final FciPayload.FciMetadata metadata = FciPayload.FciMetadata.builder()
          .artifactName(artifactName)
          .layer(layer)
          .applicationName(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_APPLICATION_NAME))
          .clientName(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_CLIENT_NAME))
          .actionName(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_ACTION))
          .criticality(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_CRITICALITY))
          .transactionId(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_TRANSACTION_ID))
          .parentTransactionId(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_PARENT_TRANSACTION_ID))
          .channel(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_CHANNEL))
          .flow(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_FLOW))
          .market(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_MARKET))
          .language(getAttributeValue(attr,
              SkeletonBaggageConstants.RQ_ATTR_LANGUAGE))
          .context(getAttributeValue(attr,
              FciContextConstants.RQ_ATTR_FCI_CONTEXT))
          .build();

      payload.setMetadata(metadata);

      if (data != null && !data.isEmpty()) {
        Map<String, String> copy = Map.copyOf(data);

        payload.setData(copy);
      }

      return payload;
    }
    
    /**
     * Creates an FciPayload for initialization events from request attributes.
     * 
     * @param attr The ServletRequestAttributes containing baggage attributes
     * @param data Additional data to include in the payload (can be null)
     * @return FciPayload configured for fci_init
     */
    public final FciPayload attributeToInit(final ServletRequestAttributes attr, Map<String,String> data) {
        return toFciPayload(attr, FciContractEnum.FCI_INIT, data);
    }
    
    /**
     * Creates an FciPayload for end events from request attributes.
     *
     * @param attr The ServletRequestAttributes containing baggage attributes
     * @param data Additional data to include in the payload (can be null)
     * @return FciPayload configured for fci_end
     */
    public final FciPayload attributeToEnd(final ServletRequestAttributes attr,
        Map<String, String> data) {
        return toFciPayload(attr, FciContractEnum.FCI_END, data);
    }
    
    /**
     * Creates an FciPayload for payload events from request attributes.
     *
     * @param attr The ServletRequestAttributes containing baggage attributes
     * @param data Additional data to include in the payload (can be null)
     * @return FciPayload configured for fci_payload
     */
    public final FciPayload attributeToPayload(final ServletRequestAttributes attr,
        Map<String, String> data) {
        return toFciPayload(attr, FciContractEnum.FCI_PAYLOAD, data);
    }
    
    /**
     * Helper method to safely get attribute values from ServletRequestAttributes.
     *
     * @param attr The ServletRequestAttributes
     * @param attributeName The attribute name to retrieve
     * @return The attribute value as a String, or null if not found
     */
    private static String getAttributeValue(final ServletRequestAttributes attr,
        final String attributeName) {
        final Object value = attr.getAttribute(attributeName,
            RequestAttributes.SCOPE_REQUEST);
        return value != null ? value.toString() : null;
    }
}
