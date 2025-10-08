package com.fci.sdk.api;

import com.fci.sdk.facade.FciFacade;
import com.fci.sdk.skeleton.constant.SkeletonApiConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Example controller demonstrating how to use the FCI facade for tracking
 * customer interactions in a medical appointment booking scenario.
 * 
 * This controller shows the recommended patterns for:
 * - Initializing FCI events at the start of customer interactions
 * - Sending payload data during the interaction
 * - Finalizing FCI events with success or error information
 */
@RestController
@RequestMapping("/api/medical")
@RequiredArgsConstructor
@Slf4j
public class FciExampleController {
    /**
     * Example endpoint for searching doctors.
     * Demonstrates basic FCI usage with init and end calls.
     */
    @GetMapping("/doctors/search")
    public ResponseEntity<Map<String, Object>> searchDoctors(
            @RequestParam final String specialty,
            @RequestParam final String location,
            @RequestParam final String date,
            @RequestHeader(value = SkeletonApiConstants.API_HEADER_TRACK_ID, required = false) final String trackId,
            @RequestHeader(value = SkeletonApiConstants.API_HEADER_PARENT_TRACK_ID, required = false) final String parentTrackId) {

        try {
            FciFacade.setContext("search_doctors");
            // Simulate doctor search business logic
            final Map<String, Object> doctors = performDoctorSearch(specialty, location, date);

            return ResponseEntity.ok(doctors);

        } catch (Exception e) {
          //TODO: Add usecase of FciFacade.endWithError
          /*FciFacade.end(e);*/

            final Map<String, Object> errorResponse = new ConcurrentHashMap<>();
            errorResponse.putIfAbsent("error", "Failed to search doctors");
            errorResponse.putIfAbsent("message", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Example endpoint for booking a medical appointment.
     * Demonstrates FCI usage with payload data and complex error handling.
     */
    @PostMapping("/appointments/book")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @RequestBody final Map<String, Object> appointmentRequest,
            @RequestHeader(value = SkeletonApiConstants.API_HEADER_TRACK_ID, required = false) final String trackId,
            @RequestHeader(value = SkeletonApiConstants.API_HEADER_PARENT_TRACK_ID, required = false) final String parentTrackId) {

        // Initialize FCI event for appointment booking
        //TODO: define if needed or interceptor covers it
        //fci.init("book_appointment", SkeletonChannelEnum.WEB.getValue(),
        //  "appointment");

        try {
            // Extract appointment details
            final String doctorId = (String) appointmentRequest.get("doctorId");
            final String clientName = (String) appointmentRequest.get("clientName");
            final String email = (String) appointmentRequest.get("email");
            final String insuranceId = (String) appointmentRequest.get("insuranceId");

            // Send payload data with appointment details
            final Map<String, Object> appointmentData = new ConcurrentHashMap<>();
            appointmentData.putIfAbsent("doctorId", doctorId);
            appointmentData.putIfAbsent("clientName", clientName);
            appointmentData.putIfAbsent("email", email);
            appointmentData.putIfAbsent("insuranceId", insuranceId);

            FciFacade.setContext("book_appointment");
            //TODO: define if needed or interceptor covers it
            //fci.payload("book_appointment", ARTIFACT_CHANNEL, "appointment",
            //  appointmentData);

            // Simulate appointment booking process
            final Map<String, Object> appointmentResult = performAppointmentBooking(appointmentRequest);

            return ResponseEntity.ok(appointmentResult);

        } catch (InsuranceException e) {
            // TODO: Handle insurance-specific errors
            /*fci.endWithError("book_appointment", ARTIFACT_CHANNEL,
              "appointment",
                           "InsuranceError", e.getMessage(), "Insurance
                           coverage verification failed");
             */

            final Map<String, Object> errorResponse = new ConcurrentHashMap<>();
            errorResponse.putIfAbsent("error", "Insurance verification failed");
            errorResponse.putIfAbsent("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);

        } catch (DoctorNotAvailableException e) {
            // TODO: Handle doctor availability errors
            /*fci.endWithError("book_appointment", ARTIFACT_CHANNEL,
              "appointment",
                           "DoctorNotAvailableError", e.getMessage(), "Selected doctor is not available for the requested time");
             */

            final Map<String, Object> errorResponse = new ConcurrentHashMap<>();
            errorResponse.putIfAbsent("error", "Doctor not available");
            errorResponse.putIfAbsent("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            // Handle general errors
            //TODO: define if needed or interceptor covers it
            //fci.endWithError( e);

            final Map<String, Object> errorResponse = new ConcurrentHashMap<>();
            errorResponse.putIfAbsent("error", "Appointment booking failed");
            errorResponse.putIfAbsent("message", "An unexpected error occurred");

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Example endpoint for submitting insurance claims.
     * Demonstrates FCI usage with different channels and flows.
     */
    @PostMapping("/claims/submit")
    public ResponseEntity<Map<String, Object>> submitClaim(
            @RequestBody final Map<String, Object> claimRequest,
            @RequestParam(defaultValue = "web") final String channel,
            @RequestHeader(value = SkeletonApiConstants.API_HEADER_TRACK_ID, required = false) final String trackId,
            @RequestHeader(value = SkeletonApiConstants.API_HEADER_PARENT_TRACK_ID, required = false) final String parentTrackId) {

        // Initialize FCI event for claim submission
        //TODO: define if needed or interceptor covers it
        // fci.init("submit_claim", channel, "claims");

        try {
            // Simulate claim submission process
            final Map<String, Object> claimResult = performClaimSubmission(claimRequest);

            return ResponseEntity.ok(claimResult);

        } catch (Exception e) {
            // TODO: End FCI event with error
            //fci.endWithError("submit_claim", channel, "claims", e);

            final Map<String, Object> errorResponse = new ConcurrentHashMap<>();
            errorResponse.putIfAbsent("error", "Claim submission failed");
            errorResponse.putIfAbsent("message", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Example endpoint for prescription refills.
     * Demonstrates FCI usage for prescription management.
     */
    @PostMapping("/prescriptions/refill")
    public ResponseEntity<Map<String, Object>> refillPrescription(
            @RequestBody final Map<String, Object> refillRequest,
            @RequestHeader(value = SkeletonApiConstants.API_HEADER_TRACK_ID, required = false) final String trackId,
            @RequestHeader(value = SkeletonApiConstants.API_HEADER_PARENT_TRACK_ID, required = false) final String parentTrackId) {

        // Initialize FCI event for prescription refill
        //TODO: define if needed or interceptor covers it
        // fci.init("refill_prescription", MOBILE_CHANNEL, "prescriptions");

        try {
            // Simulate prescription refill process
            final Map<String, Object> refillResult = performPrescriptionRefill(refillRequest);

            //TODO: End FCI event successfully
            //fci.end("refill_prescription", MOBILE_CHANNEL, "prescriptions");

            return ResponseEntity.ok(refillResult);

        } catch (Exception e) {
            // TODO: End FCI event with error

            /* fci.endWithError("refill_prescription", MOBILE_CHANNEL,
              "prescriptions", e);
             */

            final Map<String, Object> errorResponse = new ConcurrentHashMap<>();
            errorResponse.putIfAbsent("error", "Prescription refill failed");
            errorResponse.putIfAbsent("message", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // Simulated business logic methods

    private Map<String, Object> performDoctorSearch(final String specialty, final String location, final String date) {
        try {
            // This is a terrible idea in production code since it uses threads and is not J2EE compliant
            Thread.sleep(100); //NOPMD
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //NOPMD
        }

        final Map<String, Object> result = new ConcurrentHashMap<>();
        result.putIfAbsent("doctors", new Object[]{
            Map.of("id", "DR001", "name", "Dr. Smith", "specialty", specialty, "location", location, "available", true),
            Map.of("id", "DR002", "name", "Dr. Johnson", "specialty", specialty, "location", location, "available", true)
        });
        result.put("total", 2);

        return result;
    }

    private Map<String, Object> performAppointmentBooking(final Map<String, Object> appointmentRequest) {
        try {
            // This is a terrible idea in production code since it uses threads and is not J2EE compliant
            Thread.sleep(200); //NOPMD
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //NOPMD
        }

        // Simulate potential errors
        final String doctorId = (String) appointmentRequest.get("doctorId");
        final String insuranceId = (String) appointmentRequest.get("insuranceId");

        if ("DR001".equals(doctorId)) {
            throw new InsuranceException("Insurance coverage not found for this provider");
        }
        if ("DR003".equals(doctorId)) {
            throw new DoctorNotAvailableException("Doctor is not available for the requested time slot");
        }
        if ("INVALID".equals(insuranceId)) {
            throw new InsuranceException("Invalid insurance ID provided");
        }

        final Map<String, Object> result = new ConcurrentHashMap<>();
        result.putIfAbsent("appointmentId", "APT" + System.currentTimeMillis());
        result.putIfAbsent("status", "confirmed");
        result.putIfAbsent("doctorId", doctorId);
        result.putIfAbsent("appointmentDate", appointmentRequest.get("appointmentDate"));

        return result;
    }

    private Map<String, Object> performClaimSubmission(final Map<String, Object> claimRequest) {
        try {
            // This is a terrible idea in production code since it uses threads and is not J2EE compliant
            Thread.sleep(300); //NOPMD
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //NOPMD
        }

        final Map<String, Object> result = new ConcurrentHashMap<>();
        result.putIfAbsent("claimId", "CLM" + System.currentTimeMillis());
        result.putIfAbsent("status", "submitted");
        result.putIfAbsent("estimatedProcessingTime", "5-7 business days");
        result.putIfAbsent("claimAmount", claimRequest.get("amount"));

        return result;
    }

    private Map<String, Object> performPrescriptionRefill(final Map<String, Object> refillRequest) {
        // Simulate some processing time
        try {
            // This is a terrible idea in production code since it uses threads and is not J2EE compliant
            Thread.sleep(400); //NOPMD
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //NOPMD
        }

        final Map<String, Object> result = new ConcurrentHashMap<>();
        result.putIfAbsent("refillId", "REF" + System.currentTimeMillis());
        result.putIfAbsent("status", "approved");
        result.putIfAbsent("medication", refillRequest.get("medication"));
        result.putIfAbsent("quantity", refillRequest.get("quantity"));
        result.put("pickupLocation", "Pharmacy #123");

        return result;
    }

    // Custom exception classes for demonstration

    /**
     * Custom exception for insurance-related errors.
     */
    public static class InsuranceException extends RuntimeException {
        
        private static final long serialVersionUID = 8577542562153995464L;
        
        /**
         * Constructs a new InsuranceException with the specified message.
         *
         * @param message the detail message
         */
        public InsuranceException(final String message) {
            super(message);
        }
    }

    /**
     * Custom exception for doctor availability errors.
     */
    public static class DoctorNotAvailableException extends RuntimeException {
        
        private static final long serialVersionUID = 8577532562153995463L;
        
        /**
         * Constructs a new DoctorNotAvailableException with the specified message.
         *
         * @param message the detail message
         */
        public DoctorNotAvailableException(final String message) {
            super(message);
        }
    }
} 
