package com.fci.sdk.api.advice;

import com.fci.sdk.facade.FciFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class FciExampleControllerAdvice {

  private final FciFacade fci;

  /**
   * Handles business exceptions from the FCI example controller.
   *
   * @param ex the business exception
   * @return ProblemDetail with appropriate status and message
   */
  @ExceptionHandler(RuntimeException.class)
  ProblemDetail handleInsuranceException(RuntimeException ex) {
    log.warn("Insurance exception occurred: {}", ex.getMessage());

    // Extract FCI context from request and end with error
    fci.end(ex);

    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle("Insurance verification failed");
    problemDetail.setDetail(ex.getMessage());
    return problemDetail;
  }

//  /**
//   * Handles doctor availability exceptions from the FCI example controller.
//   *
//   * @param exception the doctor availability exception
//   * @return ProblemDetail with appropriate status and message
//   */
//  @ExceptionHandler(DoctorNotAvailableException.class)
//  ProblemDetail handleDoctorNotAvailableException(
//      DoctorNotAvailableException exception) {
//    log.warn("Doctor availability exception occurred: {}",
//        exception.getMessage());
//
//    fci.end(Map.of("error", "Doctor not available"));
//
//    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
//    problemDetail.setTitle("Doctor not available");
//    problemDetail.setDetail(exception.getMessage());
//    return problemDetail;
//  }

  /**
   * General exception handler for all unhandled exceptions. This will call
   * fci.endWithError and return a consistent error response.
   *
   * @param ex the exception that occurred
   * @return ResponseEntity with internal server error and consistent error
   * format
   */
  @ExceptionHandler(Exception.class)
  ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

    fci.end(ex);

    return ResponseEntity.internalServerError().body(Map.of("error","An "
        + "unexpected error occurred","message", ex.getMessage()));
  }
}