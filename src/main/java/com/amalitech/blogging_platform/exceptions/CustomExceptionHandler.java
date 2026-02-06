package com.amalitech.blogging_platform.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(RessourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(RessourceNotFoundException ex, WebRequest request) {
    ErrorResponse response = new ErrorResponse(ex.getMessage(), request.getContextPath());
    log.error(ex.getMessage(), request.getContextPath());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }


  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflictException(DataConflictException ex, WebRequest request) {
    ErrorResponse response = new ErrorResponse(ex.getMessage(), request.getContextPath());
    log.error(ex.getMessage(), request.getContextPath());
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
    ErrorResponse response = new ErrorResponse(ex.getMessage(), request.getContextPath());
    log.error(ex.getMessage(), request.getContextPath());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
    ErrorResponse response = new ErrorResponse(ex.getMessage(), request.getContextPath());
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
    response.setDescription(errors);
    log.error(ex.getMessage(), request.getContextPath());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ErrorResponse> handlePropertyReferenceException(PropertyReferenceException ex, WebRequest request) {
    ErrorResponse response = new ErrorResponse(ex.getMessage(), request.getContextPath());
    log.error(ex.getMessage(), request.getContextPath());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidDataAccessApiUsageException.class)
  public ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex, WebRequest request) {
    ErrorResponse response = new ErrorResponse(ex.getMessage(), request.getContextPath());
    log.error(ex.getMessage(), request.getContextPath());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
 }
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedtException(UnauthorizedException ex, WebRequest request) {
    ErrorResponse response = new ErrorResponse(ex.getMessage(), request.getContextPath());
    log.error(ex.getMessage(), request.getContextPath());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }
}
