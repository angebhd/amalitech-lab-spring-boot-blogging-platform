package com.amalitech.blogging_platform.exceptions;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class CustomGraphQLExceptionHandler {

  @GraphQlExceptionHandler(RessourceNotFoundException.class)
  public GraphQLError handleNotFoundException(RessourceNotFoundException ex) {
    log.warn("Resource not found", ex);

    return GraphqlErrorBuilder.newError()
            .message(ex.getMessage())
            .errorType(ErrorType.DataFetchingException)
            .extensions(Map.of(
                    "code", "NOT_FOUND"
            ))
            .build();
  }

  @GraphQlExceptionHandler(DataConflictException.class)
  public GraphQLError handleConflictException(DataConflictException ex) {
    log.warn("Data conflict", ex);

    return GraphqlErrorBuilder.newError()
            .message(ex.getMessage())
            .errorType(ErrorType.DataFetchingException)
            .extensions(Map.of(
                    "code", "CONFLICT"
            ))
            .build();
  }

  @GraphQlExceptionHandler(MethodArgumentNotValidException.class)
  public GraphQLError handleValidationException(MethodArgumentNotValidException ex) {
    log.warn("Validation error", ex);

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
            .getFieldErrors()
            .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

    return GraphqlErrorBuilder.newError()
            .message("Validation failed")
            .errorType(ErrorType.ValidationError)
            .extensions(Map.of(
                    "code", "VALIDATION_ERROR",
                    "fields", fieldErrors
            ))
            .build();
  }

  @GraphQlExceptionHandler(Exception.class)
  public GraphQLError handleGenericException(Exception ex) {
    log.error("Unexpected GraphQL error", ex);

    return GraphqlErrorBuilder.newError()
            .message("Internal server error")
            .errorType(ErrorType.DataFetchingException)
            .extensions(Map.of(
                    "code", "INTERNAL_ERROR"
            ))
            .build();
  }
}
