package com.fci.sdk.validation;

import com.fci.sdk.exception.FciMissingActionNameException;
import com.fci.sdk.exception.FciMissingArtifactNameException;
import com.fci.sdk.exception.FciMissingChannelException;
import com.fci.sdk.exception.FciMissingCriticalityException;
import com.fci.sdk.exception.FciMissingFlowException;
import com.fci.sdk.exception.FciMissingLayerDefinitionException;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;

/**
 * Utility class for validating FCI field values.
 * Reuses validation logic from SkeletonBaggageValidators for consistency.
 */
@UtilityClass
public final class FciValidationUtils {

    /**
     * Validates that the artifact name is configured.
     *
     * @param artifactName the artifact name to validate
     * @throws FciMissingArtifactNameException if the artifact name is not configured
     */
    public static void validateArtifactName(final String artifactName) throws FciMissingArtifactNameException {
        if (Strings.isBlank(artifactName)) {
            throw new FciMissingArtifactNameException("Artifact name is not configured. Please set 'spring.application.name' property.");
        }
    }

    /**
     * Validates that the layer parameter is not null or empty.
     *
     * @param layer the layer parameter to validate
     * @throws FciMissingLayerDefinitionException if the layer parameter is null or empty
     */
    public static void validateLayer(final String layer) throws FciMissingLayerDefinitionException {
        if (Strings.isBlank(layer)) {
            throw new FciMissingLayerDefinitionException("Layer parameter cannot be null or empty.");
        }
    }

    /**
     * Validates that the criticality parameter is not null or empty.
     *
     * @param criticality the criticality parameter to validate
     * @throws FciMissingCriticalityException if the criticality parameter is null or empty
     */
    public static void validateCriticality(final String criticality) throws FciMissingCriticalityException {
        if (Strings.isBlank(criticality)) {
            throw new FciMissingCriticalityException("Criticality parameter cannot be null or empty.");
        }
    }

    /**
     * Validates that the required parameters are not null or empty.
     *
     * @param actionName the action name to validate
     * @param channel the channel to validate
     * @param flow the flow to validate
     * @throws FciMissingActionNameException if the action name is null or empty
     * @throws FciMissingChannelException if the channel is null or empty
     * @throws FciMissingFlowException if the flow is null or empty
     */
    public static void validateParameters(final String actionName, final String channel, final String flow) 
            throws FciMissingActionNameException, FciMissingChannelException, FciMissingFlowException {

        if (Strings.isBlank(actionName)) {
            throw new FciMissingActionNameException("Action name cannot be null or empty.");
        }

        if (Strings.isBlank(channel)) {
            throw new FciMissingChannelException("Channel cannot be null or empty.");
        }

        if (Strings.isBlank(flow)) {
            throw new FciMissingFlowException("Flow cannot be null or empty.");
        }
    }

    //TODO: Implement validation for all values at once
}
