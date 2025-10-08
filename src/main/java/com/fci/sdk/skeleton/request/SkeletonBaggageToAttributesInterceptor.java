package com.fci.sdk.skeleton.request;

import com.dummy.sdk.skeleton.exception.SkeletonRequestValidationException;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.context.Context;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.dummy.sdk.skeleton.request.SkeletonBaggageConstants.*;


/**
 * Interceptor that validates baggage fields from OpenTelemetry context for all controllers.
 * <p>
 * This interceptor runs before each controller method and validates that all required
 * baggage fields are present and have valid values according to the skeleton specification.
 * <p>
 * If validation fails, the request is rejected with a 400 Bad Request response.
 * If validation succeeds, the baggage values are stored as request HttpServletRequest Attributes.
 * <p>
 * Using Order(1000) ensures this interceptor runs before other custom interceptors.
 * Ordering for interceptors is not ensured by default in Spring MVC so we have custom code in
 * {@link com.dummy.sdk.skeleton.config.InterceptorOrderingConfig} to enforce ordering.
 * <p>
 * 1-999: Your custom interceptors that execute before skeleton.
 * 1000: Skeleton interceptor (this one)
 * 1001-1999: Your custom interceptors that execute before FCI but after Skeleton
 * 2000: FCI interceptor
 * 2001-2999: Your custom interceptors that execute after FCI
 */
@Component
@Order(1000) // Ensure this runs before other interceptors
@Slf4j
public class SkeletonBaggageToAttributesInterceptor implements HandlerInterceptor {

  /** Application artifact name configured from properties. */
  @Value("${spring.application.name}")
  private String artifactName;

  @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws SkeletonRequestValidationException {
        final Context currentOtelContext = Context.current();
        final Baggage baggage = Baggage.fromContext(currentOtelContext);

        // Validate all required fields exist as OTEL baggage
        if (SkeletonBaggageValidators.isValidRequestBaggage(baggage)) {
            log.debug("FCI baggage validation passed for request: {}", request.getRequestURI());
            // Extract OpenTelemetry baggage from the current context

            storeBaggageInRequestAttributes(request, baggage);

            // Continue with request processing
            // Read: https://www.baeldung.com/spring-mvc-handlerinterceptor
            return true;
        }
        return false;
    }

    /**
     * Stores validated baggage values in request attributes for controllers to access.
     *
     * @param request the HTTP request
     * @param baggage the validated baggage values
     * <p>
     * This method could be a for loop but has been unrolled for performance.
     * <p>
     * Performance is tested in {@link SkeletonBaggageToAttributesInterceptorTest}
     */
    void storeBaggageInRequestAttributes(HttpServletRequest request,
        Baggage baggage) {
      // These are fields that are mapped from configuration
      request.setAttribute(SkeletonBaggageConstants.REQUEST_ATTRIBUTE_ARTIFACT_NAME,
          artifactName);
      // These are fields mapped from OTEL baggage
      request.setAttribute(RQ_ATTR_APPLICATION_NAME, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_APPLICATION_NAME));
      request.setAttribute(RQ_ATTR_CLIENT_NAME, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CLIENT_NAME));
      request.setAttribute(RQ_ATTR_ACTION, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_ACTION));
      request.setAttribute(RQ_ATTR_CRITICALITY, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CRITICALITY));
      request.setAttribute(RQ_ATTR_CHANNEL, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CHANNEL));
      request.setAttribute(RQ_ATTR_FLOW, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_FLOW));
      request.setAttribute(RQ_ATTR_TRANSACTION_ID, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_TRANSACTION_ID));
      request.setAttribute(RQ_ATTR_PARENT_TRANSACTION_ID, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_PARENT_TRANSACTION_ID));
      request.setAttribute(RQ_ATTR_MARKET, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_MARKET));
      request.setAttribute(RQ_ATTR_LANGUAGE, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_LANGUAGE));
      request.setAttribute(RQ_ATTR_DEVICE_ID, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_DEVICE_ID));
      request.setAttribute(RQ_ATTR_USER_ID, baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_USER_ID));
    }
}
