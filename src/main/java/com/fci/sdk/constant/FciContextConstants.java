package com.fci.sdk.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FciContextConstants {
  public static final String RQ_ATTR_FCI_CONTEXT_VOID = "**no fci "
      + "context set for fci**";

  public static final String RQ_ATTR_FCI_CONTEXT = "fci:context";

  /** This is a transaction id used when no attributes are available for
   * context */
  public static final String FCI_ERROR_TRANSACTION_ID_NO_ATTRIBUTES =
      "00000000000040008bad00000badc0de";

  /** This is a transaction id used when getting context or attributes is
   * impossible; it can be used in logs to track framework bugs */
  public static final String FCI_ERROR_TRANSACTION_ID_MISSING_ATTRIBUTE =
      "00000000000040008bad01000badc0de";
}
