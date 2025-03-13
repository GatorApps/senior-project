package org.gatorapps.garesearch.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import org.gatorapps.garesearch.dto.ErrorResponse;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        System.out.println("in method agr nott valid exception");
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse<Map<String, String>> errResponse = new ErrorResponse<>("ERR_INPUT_FAIL_VALIDATION", "Please fix the following errors and try again", errors);
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation: ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        ErrorResponse<Map<String, String>> errResponse = new ErrorResponse<>("ERR_INPUT_FAIL_VALIDATION", "Please fix the following errors and try again", errors);
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse<Void>> handleResourceNotFoundExceptions(ResourceNotFoundException ex){
        ErrorResponse<Void> errResponse = new ErrorResponse<>(ex.getErrCode(), ex.getMessage());
        return new ResponseEntity<>(errResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MalformedParamException.class)
    public ResponseEntity<ErrorResponse<Void>> handleMalformedParamsException(MalformedParamException ex) {
        ErrorResponse<Void> errResponse = new ErrorResponse<>(ex.getErrCode(), ex.getMessage());
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnwantedResult.class)
    public ResponseEntity<ErrorResponse<Void>> handleUnwantedResult(UnwantedResult ex) {
        ErrorResponse<Void> errResponse = new ErrorResponse<>(ex.getErrCode(), ex.getMessage());
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    // when @RequestParam is required but missing
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse<Void>> handleMissingParamsException(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        ErrorResponse<Void> errResponse = new ErrorResponse<>("ERR_REQ_MISSING_REQUIRED_PARAM", "Missing required req params: " + paramName);
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    // when @RequestParam is found invalid (example: the @Pattern check for getStudentApplications in ApplicationController)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse<Void>> handleMethodValidationExceptions(HandlerMethodValidationException ex) {
        String errMessage = ex.getAllValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse<Void> errResponse = new ErrorResponse<>("ERR_INPUT_FAIL_VALIDATION", errMessage);

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<Void>> handleGenericException(Exception ex){
        ErrorResponse<Void> errResponse = new ErrorResponse<>("ERR_ISE_CATCH_ALL", ex.getMessage());
        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
