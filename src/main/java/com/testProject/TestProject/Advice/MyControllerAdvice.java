package com.testProject.TestProject.Advice;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class MyControllerAdvice {

    // Handle SQL Exception
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Status 500 Internal Server Error
    public ResponseEntity<String> handleSQLException(SQLException ex) {
        System.out.println("SQL Exception occurred: " + ex.getMessage());
        return new ResponseEntity<>("Database error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Status 404 Not Found
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        // Log or print the exception message (optional)
        System.out.println("Entity not found: " + ex.getMessage());
        return new ResponseEntity<>("Entity not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Handle NullPointerException (for unexpected null values in application)
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Status 400 Bad Request
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        // Log or print the exception message (optional)
        System.out.println("Null pointer exception: " + ex.getMessage());
        return new ResponseEntity<>("Null pointer error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Handle Method Not Allowed Exception (HTTP 405)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED) // Status 405 Method Not Allowed
    public ResponseEntity<String> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        // Log or print the exception message (optional)
        System.out.println("Method Not Allowed: " + ex.getMessage());
        return new ResponseEntity<>("Method Not Allowed: " + ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handle Access Denied Exception (HTTP 403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // Status 403 Forbidden
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        // Log or print the exception message (optional)
        System.out.println("Access Denied: " + ex.getMessage());
        return new ResponseEntity<>("Access Denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // Handle any other generic exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Status 500 Internal Server Error
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // Log or print the exception message (optional)
        System.out.println("Generic exception: " + ex.getMessage());
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
