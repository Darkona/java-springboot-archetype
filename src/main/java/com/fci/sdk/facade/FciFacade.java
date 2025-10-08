package com.fci.sdk.facade;

import com.fci.sdk.constant.FciContextConstants;
import com.fci.sdk.dto.FciPayload;
import com.fci.sdk.mapper.FciOpenTelemetryMapper;
import com.fci.sdk.mapper.FciRequestAttributesMapper;
import com.fci.sdk.service.FciService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Facade for Failed Customer Interactions (FCI) that provides a simple
 * interface for developers to track customer interactions, similar to a logging
 * framework.
 * <p>
 * This facade abstracts the complexity of FCI event creation and provides
 * convenient methods for common use cases. It automatically populates FCI
 * events from OpenTelemetry context and request attributes.
 */
@Component
@Slf4j
public class FciFacade {

    private final FciService fciService;
    private final FciOpenTelemetryMapper openTelemetryMapper;
    private final FciRequestAttributesMapper requestAttributesMapper;

    /**
     * Application artifact name configured from properties.
     */
    @Value("${spring.application.name}")
    private String artifactName;

    /**
     * Default service layer for FCI events.
     */
    @Value("${fci.default.layer}")
    private String defaultLayer;

    public FciFacade(FciService fciService,
                     FciRequestAttributesMapper requestAttributesMapper,
                     FciOpenTelemetryMapper openTelemetryMapper) {
        this.fciService = fciService;
        this.requestAttributesMapper = requestAttributesMapper;
        this.openTelemetryMapper = openTelemetryMapper;
    }

    public final CompletableFuture<Void> init() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final FciPayload initPayload =
                requestAttributesMapper.attributeToInit(attributes, null);

        return fciService.send(initPayload);
    }

    /**
     * Sends the current context as an FCI event.
     */

    public final CompletableFuture<Void> payload() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final FciPayload currentPayload =
                requestAttributesMapper.attributeToPayload(attributes, null);

        return fciService.send(currentPayload);
    }


    /**
     * Sends the current context as an FCI event with extra data.
     *
     * @param data Additional data to include in the payload
     */

    public final CompletableFuture<Void> payload(Map<String, String> data) {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final FciPayload payload =
                requestAttributesMapper.attributeToPayload(attributes, data);

        return fciService.send(payload);
    }

    /**
     * Sends a specific payload during an ongoing customer interaction.
     *
     * @param payload The FCI payload containing the data to be sent
     */
    public final CompletableFuture<Void> payload(FciPayload payload) {
        return fciService.send(payload);
    }

    /**
     * Finalizes an FCI event indicating successful completion of a customer
     * interaction.
     *
     */
    public final CompletableFuture<Void> end() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final FciPayload payload =
                requestAttributesMapper.attributeToEnd(attributes, null);

        return fciService.send(payload);
    }

    /**
     * Sends the current context as an FCI event with extra data.
     *
     * @param data Additional data to include in the payload
     */

    public final CompletableFuture<Void> end(Map<String, String> data) {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final FciPayload payload =
                requestAttributesMapper.attributeToEnd(attributes, data);

        return fciService.send(payload);
    }

    /**
     * Sends the current context as an FCI event with an Exception
     *
     * @param exception Exception in the context of the FCI event
     * @return CompletableFuture that completes when the event is sent
     */

    public final CompletableFuture<Void> end(Throwable exception) {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //TODO: Move this to error structure in FCI
        final Map<String, String> data = new HashMap<>();
        data.put("exception", exception.getMessage());
        data.put("cause", exception.getCause() != null ?
                          exception.getCause().getMessage() : "Exception is root cause");
        data.put("exceptionType", exception.getClass().getName());
        //TODO: Add stacktrace flattening and simplification
    /* data.put("stackTrace",
        FciValidationUtils.getStackTraceAsString(exception));
     */
        final FciPayload payload =
                requestAttributesMapper.attributeToEnd(attributes, data);

        return fciService.send(payload);
    }

    /**
     * Initializes an FCI event using the current OpenTelemetry context. This
     * method automatically populates all fields from baggage.
     */
    //TODO: Implement all OTEL alternatives
    public final void initFromOtel() {
        try {
            final FciPayload payload = openTelemetryMapper.forInit();
            fciService.send(payload);
            log.debug("FCI interaction initialized from OpenTelemetry context");
        } catch (Exception e) {
            log.error(
                    "Failed to initialize FCI interaction from OpenTelemetry context", e);
        }
    }

    /**
     * Creates a new FCI payload from ServletRequestAttributes.
     *
     * @return FciPayload with metadata from request attributes
     */
    public final FciPayload createPayloadFromAttributes() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributesMapper.attributeToPayload(attributes, null);
    }

    /**
     * Creates a new FCI payload from ServletRequestAttributes.
     *
     * @return FciPayload with metadata from request attributes
     */

    public final FciPayload createPayloadFromAttributesWithData(final ServletRequestAttributes attributes, final Map<String, String> data) {
        return requestAttributesMapper.attributeToPayload(attributes, data);
    }

    /**
     * Sets the FCI context for the current request. This method sets the context
     * value on the REQUEST_ATTRIBUTE_FCI_CONTEXT key in the current request
     * attributes.
     *
     * @param context The context string to set for the current request
     */
    public static void setContext(final String context) {
        try {
            final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                final HttpServletRequest request = attributes.getRequest();
                request.setAttribute(FciContextConstants.RQ_ATTR_FCI_CONTEXT,
                        context);
                log.debug("FCI context set to: {} for request: {}", context,
                        request.getRequestURI());
            } else {
                log.warn("No request context available to set FCI context");
            }
        } catch (Exception e) {
            log.error("Failed to set FCI context: {}", context, e);
        }
    }
}
