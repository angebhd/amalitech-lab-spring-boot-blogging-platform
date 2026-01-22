package com.amalitech.blogging_platform.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("execution(public * com.amalitech.blogging_platform.service..*.*(..))")
  public void serviceMethods() {}

  @Before("serviceMethods()")
  public void logMethodEntry(JoinPoint joinPoint) {
    String args = Arrays.toString(joinPoint.getArgs());
    logger.info("→ Entering {}.{} | args: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(), // class name
            joinPoint.getSignature().getName(), // method
            args); // argument received
  }

  @AfterReturning(pointcut = "serviceMethods()", returning = "result")
  public void logMethodSuccess(JoinPoint joinPoint, Object result) {
    logger.info("← Exiting {}.{} successfully | returned: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            result != null ? result.getClass().getName() : "null");
  }

  @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
  public void logMethodException(JoinPoint joinPoint, Throwable ex) {
    logger.error("Exception in {}.{}: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            ex.getMessage());
  }
}