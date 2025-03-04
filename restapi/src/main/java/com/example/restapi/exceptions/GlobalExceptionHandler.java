package com.example.restapi.exceptions;

import com.example.restapi.io.ErrorObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.NOT_FOUND)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorObject handleResourceNotFoundException(ResourceNotFoundException ex , WebRequest request) {
        return ErrorObject.builder()
                .errorCode("DATA_NOT_FOUND")
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(new Date())
                .build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String,Object> errorResponse = new HashMap<>();
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(field -> field.getDefaultMessage()).collect(Collectors.toList());
        errorResponse.put("statusCode",HttpStatus.BAD_REQUEST.value());
        errorResponse.put("message",errors);
        errorResponse.put("timestamp",new Date());
        errorResponse.put("errorCode","VALIDATION_FAILED");
        return new ResponseEntity<Object>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorObject handleGeneralException(ResourceNotFoundException ex , WebRequest request) {
        return ErrorObject.builder()
                .errorCode("DATA_NOT_FOUND")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .timestamp(new Date())
                .build();
    }

    @ExceptionHandler(ItemExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorObject handleItemExistsException(ItemExistsException ex , WebRequest request) {
        return ErrorObject.builder()
                .errorCode("DATA_EXISTS")
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(new Date())
                .build();
    }



}
