package com.fci.sdk.api.example;

import com.fci.sdk.facade.FciFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Example controller demonstrating how to use the FCI Controller Advice system.
 * 
 * This controller shows how to:
 * 1. Set FCI context using FciContextHelper
 * 2. Let the Controller Advice automatically handle FCI error tracking
 * 3. Remove the need for manual try-catch blocks with fci.endWithError calls
 * 
 * The FciGlobalExceptionHandler will automatically:
 * - Call fci.endWithError with the context you set
 * - Return consistent error responses
 * - Handle all exceptions consistently
 */
@RestController
@RequestMapping("/api/fci-example")
@RequiredArgsConstructor
@Slf4j
public class FciControllerAdviceExampleController {

    /**
     * Example endpoint that sets FCI context and may throw exceptions.
     * The Controller Advice will automatically handle FCI error tracking.
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchWithFciContext(
            @RequestParam String query,
            @RequestParam(defaultValue = "web") String channel) {
        
        // Set FCI context for this request for verbose exceptions
        //TODO: define if needed or interceptor covers it
        FciFacade.setContext("search_items");
        
        // Simulate business logic that might throw exceptions
        if ("error".equals(query)) {
            throw new RuntimeException("Simulated error for testing FCI tracking");
        }
        
        if ("invalid".equals(query)) {
            throw new IllegalArgumentException("Invalid search query");
        }

        FciFacade.setContext("search_items_continued");
        
        // Normal response
        Map<String, Object> result = new HashMap<>();
        result.put("query", query);
        result.put("results", new String[]{"result1", "result2", "result3"});
        result.put("total", 3);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Example endpoint that demonstrates automatic FCI context detection.
     * No FCI context is set, so the Controller Advice will analyze the URI.
     */
    @GetMapping("/reports/generate")
    public ResponseEntity<Map<String, Object>> generateReport(
            @RequestParam String type) {

      log.info("Generating report of type: {}", type);

        // No FCI context set - Controller Advice will auto-detect:
        // - action: "generate" (from URI)
        // - channel: "web" (default)
        // - flow: "reports" (from URI)
        
        if ("invalid".equals(type)) {
            throw new RuntimeException("Cannot generate report for invalid type: " + type);
        }
        
        Map<String, Object> report = new HashMap<>();
        report.put("type", type);
        report.put("generated", true);
        report.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(report);
    }
    
    /**
     * Example endpoint that shows how to override channel detection.
     */
    @PostMapping("/mobile/action")
    public ResponseEntity<Map<String, Object>> mobileAction(
            @RequestBody Map<String, Object> request) {
        
        // Override the channel to be mobile-specific
        FciFacade.setContext("mobile_action");
        
        if (request.containsKey("error")) {
            throw new RuntimeException("Mobile action failed");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("action", "completed");
        response.put("mobile", true);
        
        return ResponseEntity.ok(response);
    }
}
