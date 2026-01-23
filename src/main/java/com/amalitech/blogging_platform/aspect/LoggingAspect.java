package com.amalitech.blogging_platform.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect responsible for logging execution details of service-layer methods.
 * <p>
 * Logs method entry, successful completion, and exceptions for all public
 * methods in the {@code service} package.
 * </p>
 */
@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  /**
   * Matches all public methods in the service package and its subpackages.
   */
  @Pointcut("execution(public * com.amalitech.blogging_platform.service..*.*(..))")
  public void serviceMethods() {}

  /**
   * Logs method entry along with received arguments.
   *
   * @param joinPoint provides access to the intercepted method
   */
  @Before("serviceMethods()")
  public void logMethodEntry(JoinPoint joinPoint) {
    String args = Arrays.toString(joinPoint.getArgs());
    logger.info("→ Entering {}.{} | args: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            args);
  }

  /**
   * Logs successful method completion.
   *
   * @param joinPoint provides access to the intercepted method
   * @param result the value returned by the method, may be {@code null}
   */
  @AfterReturning(pointcut = "serviceMethods()", returning = "result")
  public void logMethodSuccess(JoinPoint joinPoint, Object result) {
    logger.info("← Exiting {}.{} successfully | returned: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            result != null ? result.getClass().getName() : "null");
  }

  /**
   * Logs exceptions thrown by service methods.
   *
   * @param joinPoint provides access to the intercepted method
   * @param ex the thrown exception
   */
  @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
  public void logMethodException(JoinPoint joinPoint, Throwable ex) {
    logger.error("Exception in {}.{}: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            ex.getMessage());
  }
}
