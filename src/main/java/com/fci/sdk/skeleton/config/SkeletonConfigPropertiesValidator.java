package com.fci.sdk.skeleton.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** This class validates the configuration properties for the Skeleton SDK.
 * It can be extended to require or validate additional properties.
 */
@Validated
@ConfigurationProperties(prefix = "spring.application")
public class SkeletonConfigPropertiesValidator {
  /* The application name used as client name in requests and logging */
  @NotBlank String name;

}
