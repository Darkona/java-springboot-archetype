package com.fci.sdk.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** This class validates the configuration properties for the FCI SDK.
 * It can be extended to require or validate additional properties.
 */
@Validated
@ConfigurationProperties(prefix = "fci.default")
public class FciConfigPropsValidator {
  /* The default service layer for FCI events.
   * This is used to set the layer in the FCI metadata.
   */
  @NotBlank String layer;

}
