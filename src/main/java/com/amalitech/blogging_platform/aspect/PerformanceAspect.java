package com.amalitech.blogging_platform.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Aspect responsible for measuring and logging execution time
 * of service-layer methods.
 */
@Aspect
@Component
public class PerformanceAspect {

  private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

  /**
   * Measures execution time of service methods and logs the result.
   * <p>
   * Logs as INFO for fast executions and WARN for slow executions.
   * </p>
   *
   * @param joinPoint provides access to the intercepted method
   * @return the result of the method execution
   * @throws Throwable if the intercepted method throws an exception
   */
  @Around("execution(* com.amalitech.blogging_platform.service..*(..))")
  public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try {
      return joinPoint.proceed();
    } finally {
      stopWatch.stop();
      long timeMs = stopWatch.getTotalTimeMillis();
      String className  = joinPoint.getSignature().getDeclaringType().getSimpleName();
      String methodName = joinPoint.getSignature().getName();
      if (timeMs < 1000)
        logger.info("{}.{} took {} ms", className, methodName, timeMs);
      else
        logger.warn("{}.{} took {} s", className, methodName, (double) timeMs / 1000);
    }
  }
}
