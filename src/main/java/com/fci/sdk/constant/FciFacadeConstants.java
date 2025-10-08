package com.fci.sdk.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FciFacadeConstants {
  public static final String ERROR_MISSING_ACTION_NAME = "error.missing.action-name";
  public static final String ERROR_MISSING_PARENT_TRANSACTION_ID = "error.missing.parent-transaction-id";
  public static final String ERROR_MISSING_TRANSACTION_ID = "error.missing.transaction-id";
  public static final String ERROR_MISSING_CRITICALITY = "99";

  public static final String MDC_ACTION = "action";
  public static final String MDC_CRITICALITY = "crit";
  public static final String MDC_TRANSACTION_ID = "tid";
  public static final String MDC_PARENT_TRANSACTION_ID = "ptid";
  public static final String MDC_STACK_TRACE = "trace";

}
