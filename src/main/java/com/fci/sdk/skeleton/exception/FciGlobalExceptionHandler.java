package com.fci.sdk.skeleton.exception;

import com.dummy.sdk.fci.facade.FciFacade;
import com.dummy.sdk.skeleton.request.SkeletonBaggageConstants;
import com.dummy.skeleton.api.advice.RequestContext;
import com.dummy.skeleton.api.advice.ShortRequestContext;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.context.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Global exception handler that automatically tracks FCI errors for all
 * controllers.
 * <p>
 * This handler provides consistent error responses and automatically calls
 * fci.endWithError() when exceptions occur, extracting FCI context from: 1.
 * Request attributes (if set by controllers) 2. Request URI analysis 3. Default
 * fallback values
 * <p>
 * Controllers can set FCI context using request attributes: -
 * request.setAttribute("fci.action", "search_doctors") -
 * request.setAttribute("fci.channel", "web") - request.setAttribute("fci.flow",
 * "appointment") - request.setAttribute("fci.transactionId", "1234567890") -
 * request.setAttribute("fci.parentTransactionId", "1234567890")
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class FciGlobalExceptionHandler {

  private final FciFacade fci;

  /**
   * Handles all exceptions and provides consistent error responses with FCI
   * tracking. This method automatically calls fci.endWithError() and returns a
   * standardized error response format.
   *
   * @param ex the exception that occurred
   * @return ResponseEntity with internal server error and consistent error
   * format
   */
  @ExceptionHandler(Exception.class)
  ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
    log.error("FciGlobalExceptionHandler detected an unhandled exception: {}",
        ex.getMessage());

    // Extract FCI context and end with error
    RequestContext requestContext = extractLongContextFromOtel();
    if (requestContext != null) {
      try {
        fci.end(ex);
        //We really want to catch all exceptions here, so we can log them and end the FCI event
      } catch (Exception fciEx) { //NOPMD
        log.warn("Failed to end FCI event with error: {}", fciEx.getMessage());
      }
    }

    // Return consistent error response format
    final Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", "An unexpected error occurred");
    errorResponse.put("message", ex.getMessage());
    errorResponse.put("timestamp", System.currentTimeMillis());

    return ResponseEntity.internalServerError().body(errorResponse);
  }

  /**
   * Handles specific business exceptions with appropriate HTTP status codes.
   * Still calls fci.endWithError() for tracking purposes.
   *
   * @param ex the business exception
   * @return ResponseEntity with appropriate status and error format
   */
  @ExceptionHandler({
      IllegalArgumentException.class,
      IllegalStateException.class
  })
  ResponseEntity<Map<String, Object>> handleBusinessExceptions(Exception ex) {
    log.warn("Business exception occurred: {}", ex.getMessage());

    // Extract FCI context and end with error
    RequestContext requestContext = extractLongContextFromOtel();
    if (requestContext != null) {
      try {
        fci.end(ex);
      } catch (Exception fciEx) {
        log.error("Failed to end FCI event with error: {}", fciEx.getMessage());
      }
    }

    // Return bad request response
    final Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", "Invalid request");
    errorResponse.put("message", ex.getMessage());
    errorResponse.put("timestamp", System.currentTimeMillis());

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Extracts FCI context information from the current HTTP request. Priority
   * order: 1. OpenTelemetry baggage (preferred) 2. Request attributes (set by
   * controllers) 3. URI analysis (automatic detection from request path) 4.
   * Default fallback values
   *
   * @return FciContext containing the extracted information
   */
  private ShortRequestContext extractShortContextFromOtel() {
    Context currentOtelContext = Context.current();
    Baggage baggage = Baggage.fromContext(currentOtelContext);

    //TODO: set sensible defaults for this values
    return ShortRequestContext.builder().actionName(
            Optional.ofNullable(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_ACTION)).orElse("error.missing.action"))
        .criticality(Optional.ofNullable(baggage.getEntryValue(
            SkeletonBaggageConstants.BAGGAGE_CRITICALITY)).orElse("10"))
        .transactionId(baggage.getEntryValue(
            SkeletonBaggageConstants.BAGGAGE_TRANSACTION_ID))
        .parentTransactionId(baggage.getEntryValue(
            SkeletonBaggageConstants.BAGGAGE_PARENT_TRANSACTION_ID))
        .build();
  }

  /**
   * Extracts comprehensive FCI context information from the current HTTP request using OpenTelemetry baggage.
   * This method extracts all available context information and maps it to the RequestContext record.
   *
   * @return RequestContext containing the extracted information, or null if extraction fails
   */
  private RequestContext extractLongContextFromOtel() {
    try {
      Context currentOtelContext = Context.current();
      Baggage baggage = Baggage.fromContext(currentOtelContext);

      // Extract all available context information from OpenTelemetry baggage
      String actionName = Optional.ofNullable(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_ACTION))
          .orElse("error.missing.action");
      String channel = Optional.ofNullable(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CHANNEL))
          .orElse("error.missing.channel");
      String flow = Optional.ofNullable(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_FLOW))
          .orElse("error.missing.flow");
      String transactionId = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_TRANSACTION_ID);
      String parentTransactionId = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_PARENT_TRANSACTION_ID);
      String criticality = Optional.ofNullable(baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CRITICALITY))
          .orElse("10");
      String market = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_MARKET);
      String deviceId = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_DEVICE_ID);
      String userId = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_USER_ID);
      String applicationName = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_APPLICATION_NAME);
      String clientName = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_CLIENT_NAME);
      String language = baggage.getEntryValue(SkeletonBaggageConstants.BAGGAGE_LANGUAGE);

      return new RequestContext(
          actionName, channel, flow, transactionId, parentTransactionId, criticality,
          market, deviceId, userId, applicationName, clientName, language);
    } catch (Exception e) {
      log.warn("Failed to extract long context from OpenTelemetry: {}", e.getMessage());
      return null;
    }
  }
}
