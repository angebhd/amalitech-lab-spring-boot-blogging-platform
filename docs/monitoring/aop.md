# Aspect-Oriented Programming (AOP) Implementation

This project utilizes Spring AOP to modularize cross-cutting concerns such as logging and performance monitoring. By separating these concerns from the business logic, we ensure cleaner service methods and consistent behavior across the application.

## 1. Logging Aspect

The `LoggingAspect` provides automated logging for all public methods within the service layer (`com.amalitech.blogging_platform.service`).

### Features
- **Method Entry**: Logs the method name and arguments.
- **Method Exit**: Logs the successful completion of the method and the type of the return value.
- **Exceptions**: Captures and logs exceptions thrown by service methods, including the exception message.

### Configuration
- **Pointcut**: `execution(public * com.amalitech.blogging_platform.service..*.*(..))`
- **Log Level**: INFO for entry/exit, ERROR for exceptions.

## 2. Performance Aspect

The `PerformanceAspect` measures the execution time of service layer methods to help identify performance bottlenecks.

### Features
- **Execution Time Measurement**: Wraps method execution with a timer.
- **Threshold-based Logging**:
  - **Info**: Methods taking less than 1000ms are logged at the INFO level.
  - **Warning**: Methods taking 1 second or longer are logged at the WARN level to highlight potential issues.

### Configuration
- **Pointcut**: `execution(* com.amalitech.blogging_platform.service..*(..))`
