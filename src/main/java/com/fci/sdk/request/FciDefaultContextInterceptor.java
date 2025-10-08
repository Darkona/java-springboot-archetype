package com.fci.sdk.request;

import com.fci.sdk.constant.FciContextConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to set default fci context when we receive a request
 * <p>
 * Using Order(2000) ensures this interceptor runs after the baggage to
 * attribute interceptor provided by the skeleton which should run with
 * Order 1000.
 * <p>
 * Ordering  for interceptors is not ensured by default in Spring MVC so we
 * have custom code in
 * {@link com.fci.sdk.config.InterceptorOrderingConfig} to enforce
 * ordering.
 * <p>
 * 1-999: Your custom interceptors that execute before skeleton.
 * 1000: Skeleton interceptor
 * 1001-1999: Your custom interceptors that execute before FCI but after
 * Skeleton
 * 2000: FCI interceptor
 * 2001-2999: Your custom interceptors that execute after FCI
 */
@Component

@Order(2000)
@Slf4j
public class FciDefaultContextInterceptor implements HandlerInterceptor {

  //TODO: This stuff is untested create an integration test that creates a
  // request

  @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
      request.setAttribute(FciContextConstants.RQ_ATTR_FCI_CONTEXT,
          FciContextConstants.RQ_ATTR_FCI_CONTEXT_VOID);

            return true;
    }
}
