package com.fci.sdk.skeleton.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Comparator;
import java.util.List;

/**
 * This class abuses the WebMvcConfigurer to order interceptors based on their
 * order value. Interceptors with lower order values will be executed first.
 */
@Configuration
public class InterceptorOrderingConfig implements WebMvcConfigurer {

  /**
   * This captures the list of interceptors defined in the application context.
   */
  private final List<HandlerInterceptor> interceptors;

  public InterceptorOrderingConfig(
      @Autowired List<HandlerInterceptor> interceptors) {
    this.interceptors = interceptors;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    //TODO: Test this actually works and precedence is used
    interceptors.stream().sorted(compareByPrecedence())
        .forEach(registry::addInterceptor);
  }

  static Comparator<HandlerInterceptor> compareByPrecedence() {
    return Comparator.comparingInt(i -> {
      int precedence = Ordered.LOWEST_PRECEDENCE;
      if (i instanceof Ordered ordered) {
        precedence = ordered.getOrder();
      }
      return precedence;
    });
  }
}
