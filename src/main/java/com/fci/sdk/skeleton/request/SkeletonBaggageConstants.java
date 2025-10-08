package com.fci.sdk.skeleton.request;

import lombok.experimental.UtilityClass;

/**
 * Centralized constants for Skeleton baggage field names used in OpenTelemetry
 * context.
 * <p>
 * This class defines all the standard field names that should be present in the
 * OpenTelemetry baggage for proper FCI tracking and validation.
 */
@UtilityClass
public final class SkeletonBaggageConstants {

  /** All Baggage fields are mapped to skel:: into HttpServletRequest
  * attributes and are used to track the request.
  * <p>
  * Baggage fields tell a story about a request from APPLICATION_NAME.
  * Received in this service from CLIENT_NAME in an attempt to complete ACTION.
  * Request is of CRITICALITY level and began on CHANNEL while navigating FLOW.
  * It is tracked with TRANSACTION_ID which originated from
  * PARENT_TRANSACTION_ID on MARKET.
  * It has been generated in DEVICE_ID authenticated as USER_ID which chose
  * LANGUAGE for I18N.
  * <p>
  * Help this story be happy and performant.
  **/
  public static final String BAGGAGE_APPLICATION_NAME = "app";
  public static final String BAGGAGE_CLIENT_NAME = "cli";
  public static final String BAGGAGE_ACTION = "action";
  public static final String BAGGAGE_CRITICALITY = "crit";
  public static final String BAGGAGE_CHANNEL = "chan";
  public static final String BAGGAGE_FLOW = "flow";
  public static final String BAGGAGE_TRANSACTION_ID = "t_id";
  public static final String BAGGAGE_PARENT_TRANSACTION_ID = "pt_id";
  public static final String BAGGAGE_MARKET = "market";
  public static final String BAGGAGE_LANGUAGE = "lang";
  public static final String BAGGAGE_DEVICE_ID = "d_id";
  public static final String BAGGAGE_USER_ID = "u_id";

  /* This is the prefix added to all request attributes
   * to avoid collisions with other request attributes.
   * It is used to map baggage fields to HttpServletRequest attributes.
   */
  public static final String ATTR_PREFIX = "skel:";
  /* This header is not mapped from baggage but recovered from spring
  .application.name
   */
  public static final String REQUEST_ATTRIBUTE_ARTIFACT_NAME = ATTR_PREFIX + "artifact";

  /**
   * HttpServletRequest attribute names for baggage fields, names are highly
   * coupled over code and mapped to request attributes in
   * {@link SkeletonBaggageToAttributesInterceptor}
   * <p>
   * Other highly coupled code might exist as headers are assumed to "never
   * change (r)"
   **/
  public static final String RQ_ATTR_APPLICATION_NAME = ATTR_PREFIX + BAGGAGE_APPLICATION_NAME;
  public static final String RQ_ATTR_CLIENT_NAME = ATTR_PREFIX + BAGGAGE_CLIENT_NAME;
  public static final String RQ_ATTR_ACTION = ATTR_PREFIX + BAGGAGE_ACTION;
  public static final String RQ_ATTR_CRITICALITY = ATTR_PREFIX + BAGGAGE_CRITICALITY;
  public static final String RQ_ATTR_CHANNEL =
      ATTR_PREFIX + BAGGAGE_CHANNEL;
  public static final String RQ_ATTR_FLOW = ATTR_PREFIX + BAGGAGE_FLOW;
  public static final String RQ_ATTR_TRANSACTION_ID =
      ATTR_PREFIX + BAGGAGE_TRANSACTION_ID;
  public static final String RQ_ATTR_PARENT_TRANSACTION_ID = ATTR_PREFIX + BAGGAGE_PARENT_TRANSACTION_ID;
  public static final String RQ_ATTR_MARKET = ATTR_PREFIX + BAGGAGE_MARKET;
  public static final String RQ_ATTR_LANGUAGE = ATTR_PREFIX + BAGGAGE_LANGUAGE;
  public static final String RQ_ATTR_DEVICE_ID = ATTR_PREFIX + BAGGAGE_DEVICE_ID;
  public static final String RQ_ATTR_USER_ID = ATTR_PREFIX + BAGGAGE_USER_ID;

  /* This is the length of a UID which is a UUID v4 minus '-'characters */
  /** UID are defined as 24 url safe characters from a-z and 0-9
   * It's a performance optimization over using UUID and can create funny
   * random strings in a much larger namespace!
   * It has better entropy than UUID does:
   * UUIDv4 has 128-bit size but only 122 random bits (6 bits are fixed for version/variant)
   * 122/log2(36) ~= 23.60 => 24 characters
   */
  public static final int UID_STRING_LENGTH = 24;

  // All required baggage fields are mapped to skel:: into HttpServletRequest request
  private static final String[] REQUIRED_BAGGAGE_FIELDS = {BAGGAGE_ACTION,
      BAGGAGE_CRITICALITY, BAGGAGE_CHANNEL, BAGGAGE_FLOW,
      BAGGAGE_TRANSACTION_ID, BAGGAGE_PARENT_TRANSACTION_ID, BAGGAGE_MARKET,
      BAGGAGE_DEVICE_ID, BAGGAGE_USER_ID, BAGGAGE_APPLICATION_NAME,
      BAGGAGE_CLIENT_NAME, BAGGAGE_LANGUAGE};

  private static final String[] MAPPED_BAGGAGE_FIELDS = {BAGGAGE_ACTION,
      BAGGAGE_CRITICALITY, BAGGAGE_CHANNEL, BAGGAGE_FLOW,
      BAGGAGE_TRANSACTION_ID, BAGGAGE_PARENT_TRANSACTION_ID, BAGGAGE_MARKET,
      BAGGAGE_DEVICE_ID, BAGGAGE_USER_ID, BAGGAGE_APPLICATION_NAME,
      BAGGAGE_CLIENT_NAME, BAGGAGE_LANGUAGE};

  /** Valid criticality values
   * 99 should not be used in user code it's reserved to signal error in
   * the framework
   */
  static final String[] VALID_CRITICALITY_VALUES = {"1", "2", "3",
      "4", "5", "6", "7", "8", "9", "10","99"};

  // Validation constants
  public static final int MAX_ENTITY_STRING_LENGTH = 100;
  public static final int MIN_ENTITY_STRING_LENGTH = 1;

  public static String[] getRequiredBaggageFields() {
    return REQUIRED_BAGGAGE_FIELDS;
  }

  public static String[] getMappedBaggageFields() {
    return MAPPED_BAGGAGE_FIELDS;
  }
}
