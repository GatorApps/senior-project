package org.gatorapps.garesearch.exception;

import org.gatorapps.garesearch.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundExceptions(ResourceNotFoundException ex){
        ErrorResponse errResponse = new ErrorResponse(ex.getErrCode(), ex.getMessage());
        return new ResponseEntity<>(errResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MalformedParamException.class)
    public ResponseEntity<?> handleMalformedParamsException(MalformedParamException ex) {
        ErrorResponse errResponse = new ErrorResponse(ex.getErrCode(), ex.getMessage());
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnwantedResult.class)
    public ResponseEntity<?> handleUnwantedResult(UnwantedResult ex) {
        ErrorResponse errResponse = new ErrorResponse(ex.getErrCode(), ex.getMessage());
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    // when @RequestParam is required but missing
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParamsException(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        ErrorResponse errResponse = new ErrorResponse("ERR_REQ_MISSING_REQUIRED_PARAM", "Missing required req params: " + paramName);
        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex){
        ErrorResponse errResponse = new ErrorResponse("ERR_ISE_CATCH_ALL", ex.getMessage());
        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
