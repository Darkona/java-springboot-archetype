package com.fci.sdk.skeleton.request;

import com.dummy.sdk.skeleton.exception.SkeletonRequestValidationException;
import io.opentelemetry.api.baggage.Baggage;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.dummy.sdk.skeleton.request.SkeletonBaggageConstants.MAX_ENTITY_STRING_LENGTH;
import static com.dummy.sdk.skeleton.request.SkeletonBaggageConstants.MIN_ENTITY_STRING_LENGTH;
import static com.dummy.sdk.skeleton.request.SkeletonBaggageConstants.UID_STRING_LENGTH;

/**
 * Utility class for validating FCI baggage fields from OpenTelemetry context.
 * <p>
 * This class provides fast validation methods for all required baggage fields
 * without using regular expressions for performance.
 */
public final class SkeletonBaggageValidators {

  private SkeletonBaggageValidators() {
    // Utility class - prevent instantiation
  }

  // Static validation map for performance
  private static final Map<String, Predicate<String>> VALIDATORS_MAP =
      new HashMap<>();

  // Sorted arrays for binary search performance
  private static final String[] SORTED_VALID_CRITICALITY_VALUES;
  private static final String[] SORTED_REQUIRED_FIELDS;

  static {
    // Initialize validators map
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_APPLICATION_NAME,
        SkeletonBaggageValidators::isValidApplicationName);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_CLIENT_NAME,
        SkeletonBaggageValidators::isValidClientName);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_ACTION,
        SkeletonBaggageValidators::isValidAction);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_CRITICALITY,
        SkeletonBaggageValidators::isValidCriticality);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_CHANNEL,
        SkeletonBaggageValidators::isValidChannel);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_FLOW,
        SkeletonBaggageValidators::isValidFlow);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_TRANSACTION_ID,
        SkeletonBaggageValidators::isValidTransactionId);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_PARENT_TRANSACTION_ID,
        SkeletonBaggageValidators::isValidParentTransactionId);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_MARKET,
        SkeletonBaggageValidators::isValidIsoAlpha2);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_DEVICE_ID,
        SkeletonBaggageValidators::isValidDeviceId);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_USER_ID,
        SkeletonBaggageValidators::isValidUserId);
    VALIDATORS_MAP.put(SkeletonBaggageConstants.BAGGAGE_LANGUAGE,
        SkeletonBaggageValidators::isValidLang);

    // Create sorted arrays for binary search performance
    SORTED_VALID_CRITICALITY_VALUES = Arrays.copyOf(
        SkeletonBaggageConstants.VALID_CRITICALITY_VALUES,
        SkeletonBaggageConstants.VALID_CRITICALITY_VALUES.length);
    SORTED_REQUIRED_FIELDS =
        SkeletonBaggageConstants.getRequiredBaggageFields();

    // Sort arrays for binary search
    Arrays.sort(SORTED_VALID_CRITICALITY_VALUES);
    Arrays.sort(SORTED_REQUIRED_FIELDS);
  }

  /**
   * Validates that all required baggage fields are present and have valid
   * values.
   *
   * @param baggage the OpenTelemetry baggage to validate
   * @return boolean true if all required fields are valid, false otherwise
   * @throws SkeletonRequestValidationException if any required field is missing
   *                                            or invalid
   */
  public static boolean isValidRequestBaggage(Baggage baggage) {
    if (baggage.isEmpty()) {
      throw new SkeletonRequestValidationException(
          "Missing required baggage fields. Your request is incomplete. RTFM.");
    }

    // Check if all required fields are present and valid
    for (String requiredField : SORTED_REQUIRED_FIELDS) {
      String fieldValue = baggage.getEntryValue(requiredField);

      if (!VALIDATORS_MAP.get(requiredField).test(fieldValue)) {
        throw new SkeletonRequestValidationException(
            "Invalid field value for: " + requiredField);
      }
    }

    return true;
  }

  /**
   * Validates criticality values against the predefined list.
   *
   * @param value the criticality value to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidCriticality(String value) {
    // Use binary search for performance
    return value!=null
        && Arrays.binarySearch(SORTED_VALID_CRITICALITY_VALUES, value) >= 0;
  }

  /**
   * Validates channel values against the predefined list.
   *
   * @param channel the channel value to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidChannel(String channel) {
    return isValidEntity(channel);
  }

  /**
   * Validates action names for FCI events.
   * Only lowercase letters, hyphens, and dots are allowed.
   * Length must be between 1-100 characters.
   *
   * @param action the action name to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidAction(String action) {
    return isValidEntity(action);
  }

  /**
   * Validates flow identifiers for FCI events.
   * Only lowercase letters, hyphens, and dots are allowed.
   * Length must be between 1-100 characters.
   *
   * @param flow the flow identifier to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidFlow(String flow) {
    return isValidEntity(flow);
  }

  /**
   * Validates application names for FCI events.
   * Only lowercase letters, hyphens, and dots are allowed.
   * Length must be between 1-100 characters.
   *
   * @param applicationName the application name to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidApplicationName(String applicationName) {
    return isValidEntity(applicationName);
  }

  /**
   * Validates client names for FCI events.
   * Only lowercase letters, hyphens, and dots are allowed.
   * Length must be between 1-100 characters.
   *
   * @param clientName the client name to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidClientName(String clientName) {
    return isValidEntity(clientName);
  }

  /**
   * Validates transaction IDs for FCI events.
   * Expects strings with only hexadecimal characters (a-f, 0-9), no hyphens.
   * Length must be between 1-100 characters.
   *
   * @param transactionId the transaction ID to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidTransactionId(String transactionId) {
    return isValidUIDString(transactionId);
  }

  /**
   * Validates parent transaction IDs for FCI events.
   * Expects strings with only hexadecimal characters (a-f, 0-9), no hyphens.
   * Length must be between 1-100 characters.
   *
   * @param parentTransactionId the parent transaction ID to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidParentTransactionId(String parentTransactionId) {
    return isValidUIDString(parentTransactionId);
  }

  /**
   * Validates device IDs for FCI events.
   * Expects strings with only hexadecimal characters (a-f, 0-9), no hyphens.
   * Length must be between 1-100 characters.
   *
   * @param deviceId the device ID to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidDeviceId(String deviceId) {
    return isValidUIDString(deviceId);
  }

  /**
   * Validates user IDs for FCI events.
   * Expects strings with only hexadecimal characters (a-f, 0-9), no hyphens.
   * Length must be between 1-100 characters.
   *
   * @param userId the user ID to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidUserId(String userId) {
    return isValidUIDString(userId);
  }

  /**
   * Validates hexadecimal strings (transaction IDs, device IDs, user IDs).
   * Expects strings with only hexadecimal characters, no hyphens.
   *
   * @param uid the hexadecimal string to validate
   * @return true if valid, false otherwise
   */
  static boolean isValidUIDString(String uid) {
    //Length 0 for null or empty strings
    final int length = uid==null?0:uid.length();
    int iterator = 0;
    boolean isValid = (length == UID_STRING_LENGTH);
    char charAt;

    while (iterator < length && isValid) {
      charAt = uid.charAt(iterator);
      isValid = (charAt >= 'a' && charAt <= 'z')
          || (charAt >= '0' && charAt <= '9');
      iterator++;
    }

    return isValid;
  }

  /**
   * Validates ISO 3166 alpha-2 codes (market, language).
   * Only lowercase characters are allowed.
   * "uy" is ok, "UY" is not.
   *
   * @param value the ISO code to validate
   * @return true if valid, false otherwise
   */
  //TODO: Add performance test against character range (which might drop
  // some UTF-8 characters in lew of performance)
  static boolean isValidIsoAlpha2(String value) {
    //Not null, exactly 2 characters
    return value != null && value.length() == 2
        //both lowercase letters
        && Character.getType(value.charAt(0)) == Character.LOWERCASE_LETTER &&
        Character.getType(value.charAt(1)) == Character.LOWERCASE_LETTER;
  }

  static boolean isValidLang(String lang) {
    // Not null, exactly 5 characters
    return lang != null && lang.length() == 5
        // begin with two lowercase letters
        && Character.getType(lang.charAt(0)) == Character.LOWERCASE_LETTER
        && Character.getType(lang.charAt(1)) == Character.LOWERCASE_LETTER
        // followed by an underscore
        && lang.charAt(2) == '_'
        // followed by two lowercase letters
        && Character.getType(lang.charAt(3)) == Character.LOWERCASE_LETTER
        && Character.getType(lang.charAt(4)) == Character.LOWERCASE_LETTER;
  }

  static boolean isValidEntity(String entity) {
    if (Strings.isBlank(entity)) {
      return false;
    }

    int length = entity.length();
    if (length < MIN_ENTITY_STRING_LENGTH || length > MAX_ENTITY_STRING_LENGTH) {
      return false;
    }

    for (char c : entity.toCharArray()) {
      if (((c < 'a' || c > 'z') && (c < '0' || c > '9')) && c != '-' && c != '.') {
        return false;
      }
    }

    return true;
  }

  /**
   * Gets the list of required baggage fields.
   *
   * @return array of required field names
   */
  public static String[] getRequiredFields() {
    return Arrays.copyOf(SORTED_REQUIRED_FIELDS, SORTED_REQUIRED_FIELDS.length);
  }

  /**
   * Gets the list of valid criticality values.
   *
   * @return array of valid criticality values
   */
  public static String[] getValidCriticalityValues() {
    return Arrays.copyOf(SORTED_VALID_CRITICALITY_VALUES,
        SORTED_VALID_CRITICALITY_VALUES.length);
  }
}
