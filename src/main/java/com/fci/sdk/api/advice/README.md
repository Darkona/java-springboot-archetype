# FCI Controller Advice System with OpenTelemetry Integration

This package provides a comprehensive solution for automatically tracking FCI (Failed Customer Interactions) errors using Spring's Controller Advice pattern and OpenTelemetry context. The system eliminates the need for manual `try-catch` blocks with `fci.endWithError()` calls in your controllers and automatically validates all required FCI baggage fields.

## Overview

The FCI Controller Advice system consists of several components that work together to provide comprehensive FCI tracking:

1. **`FciGlobalExceptionHandler`** - Main exception handler that automatically calls `fci.endWithError()`
2. **`FciContextHelper`** - Utility class for controllers to set FCI context
3. **`FciContext`** - Record class to hold FCI context information
4. **`FciBaggageConstants`** - Centralized constants for all baggage field names
5. **`FciBaggageValidators`** - Validation utilities for all baggage fields
6. **`FciBaggageInterceptor`** - Interceptor that validates all baggage fields for every request

## How It Works

### OpenTelemetry Integration
1. **OpenTelemetry baggage is automatically validated** for every request via `FciBaggageInterceptor`
2. **All required FCI fields are extracted** from the OpenTelemetry context
3. **Validation ensures data integrity** before any controller processing begins

### Exception Handling
1. **Controllers set FCI context** using `FciContextHelper` methods (optional fallback)
2. **Exceptions occur** during normal operation
3. **Controller Advice automatically intercepts** the exception
4. **FCI error tracking** is performed using the OpenTelemetry context
5. **Consistent error response** is returned to the client

## Benefits

- ✅ **Automatic FCI tracking** - No more manual `fci.endWithError()` calls
- ✅ **OpenTelemetry integration** - Automatic baggage validation and extraction
- ✅ **Consistent error responses** - Standardized error format across all endpoints
- ✅ **Clean controller code** - Focus on business logic, not error handling
- ✅ **Flexible context setting** - Set full context or just parts
- ✅ **Automatic fallback** - Context detection when not explicitly set
- ✅ **Zero configuration** - Works out of the box
- ✅ **Data validation** - Ensures all required FCI fields are present and valid

## Usage Examples

### Basic Usage

```java
@GetMapping("/doctors/search")
public ResponseEntity<?> searchDoctors() {
    // Set FCI context for this request
    FciContextHelper.setContext("search_doctors", "web", "appointment");
    
    // Your business logic here...
    // If an exception occurs, the Controller Advice will automatically
    // call fci.endWithError("search_doctors", "web", "appointment", ex)
    
    return ResponseEntity.ok(doctors);
}
```

### Partial Context Setting

```java
@PostMapping("/appointments/book")
public ResponseEntity<?> bookAppointment() {
    // Set only the action - channel and flow will be auto-detected
    FciContextHelper.setAction("book_appointment");
    
    // Controller Advice will auto-detect:
    // - channel: from User-Agent or headers
    // - flow: from URI path analysis
    
    return ResponseEntity.ok(appointment);
}
```

### Mobile-Specific Context

```java
@PostMapping("/mobile/action")
public ResponseEntity<?> mobileAction() {
    // Override channel for mobile operations
    FciContextHelper.setContext("mobile_action", "app.mobile", "mobile_operations");
    
    return ResponseEntity.ok(result);
}
```

### Automatic Context Detection

```java
@GetMapping("/reports/generate")
public ResponseEntity<?> generateReport() {
    // No FCI context set - Controller Advice will auto-detect:
    // - action: "generate" (from URI)
    // - channel: "web" (default)
    // - flow: "reports" (from URI)
    
    return ResponseEntity.ok(report);
}
```

## FCI Context Priority

The system uses the following priority order for determining FCI context:

1. **Request attributes** (set by `FciContextHelper`)
2. **URI analysis** (automatic detection from request path)
3. **Default values** (fallback when detection fails)

## Automatic URI Analysis

The Controller Advice automatically analyzes request URIs to determine FCI context:

| URI Pattern | Action | Flow |
|-------------|--------|------|
| `/doctors/search` | `search_doctors` | `appointment` |
| `/appointments/book` | `book_appointment` | `appointment` |
| `/claims/submit` | `submit_claim` | `claims` |
| `/prescriptions/refill` | `refill_prescription` | `prescriptions` |

## Channel Detection

Channels are automatically detected from:

1. **`X-Channel` header** (if explicitly set)
2. **User-Agent analysis**:
   - `Android` → `app.android`
   - `iPhone/iPad` → `app.ios`
   - `Mobile` → `app`
3. **Default fallback** → `web`

## Error Response Format

All errors return a consistent JSON format:

```json
{
  "error": "An unexpected error occurred",
  "message": "Specific error message",
  "timestamp": 1703123456789
}
```

## Migration from Manual FCI Handling

### Before (Manual Approach)

```java
@GetMapping("/doctors/search")
public ResponseEntity<Map<String, Object>> searchDoctors() {
    fci.init("search_doctors", "web", "appointment");
    
    try {
        // Business logic
        return ResponseEntity.ok(doctors);
    } catch (Exception e) {
        fci.endWithError("search_doctors", "web", "appointment", e);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Failed to search doctors");
        errorResponse.put("message", e.getMessage());
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
```

### After (Controller Advice Approach)

```java
@GetMapping("/doctors/search")
public ResponseEntity<Map<String, Object>> searchDoctors() {
    // Set FCI context once
    FciContextHelper.setContext("search_doctors", "web", "appointment");
    
    // Focus on business logic - error handling is automatic
    return ResponseEntity.ok(doctors);
}
```

## Configuration

No additional configuration is required. The system works out of the box with:

- Spring Boot auto-configuration
- Existing FCI facade configuration
- Standard Spring exception handling

## Best Practices

1. **Set FCI context early** in your controller methods
2. **Use descriptive action names** that match your business operations
3. **Be consistent with channel naming** across your application
4. **Group related operations** under the same flow
5. **Test error scenarios** to ensure FCI tracking works correctly

## Troubleshooting

### FCI Context Not Being Used

- Ensure `FciContextHelper.setContext()` is called before any business logic
- Check that the request is within the same thread context
- Verify that the Controller Advice is properly registered

### Automatic Detection Not Working

- Check URI patterns match the expected format
- Ensure User-Agent headers are properly set for mobile detection
- Review the automatic detection logic in `FciGlobalExceptionHandler`

### Error Responses Not Consistent

- Verify that the Controller Advice is the only exception handler
- Check for conflicting `@ExceptionHandler` methods in other classes
- Ensure proper Spring configuration

## Examples

See `FciControllerAdviceExampleController` for comprehensive usage examples covering all scenarios.

## OpenTelemetry Integration

### Required Baggage Fields

The system automatically validates that all required baggage fields are present in the OpenTelemetry context:

| Field Name | Baggage Key | Description | Validation |
|------------|-------------|-------------|------------|
| Action | `action` | Name of the action being performed | Entity string format |
| Criticality | `crit` | Criticality level (low/medium/high/critical) | Predefined values |
| Channel | `channel` | Interaction channel | Predefined values |
| Flow | `flow` | Business flow | Predefined values |
| Transaction ID | `t9n_id` | Current transaction identifier | Hexadecimal string |
| Parent Transaction ID | `parent_t9n_id` | Parent transaction identifier | Hexadecimal string |
| Market | `market` | Market identifier | ISO alpha-2 code |
| Device ID | `dev_id` | Device identifier | Hexadecimal string |
| User ID | `user_id` | User identifier | Hexadecimal string |
| Application Name | `app` | Application name | Entity string format |
| Client Name | `client` | Client name | Entity string format |

### Validation Rules

- **Entity Strings**: Lowercase letters, digits, hyphens, underscores, dots only
- **Hexadecimal Strings**: 0-9, a-f, A-F characters only (no hyphens)
- **ISO Codes**: Two-letter country/language codes (same case)
- **Enumerated Values**: Must match predefined lists for channel, flow, and criticality

### Automatic Validation

The `FciBaggageInterceptor` automatically validates all baggage fields for every request:
- Rejects requests with missing or invalid fields (400 Bad Request)
- Stores validated values in request attributes for controllers
- Ensures data integrity before any business logic execution
