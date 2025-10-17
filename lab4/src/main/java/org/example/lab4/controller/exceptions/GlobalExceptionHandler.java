package org.example.lab4.controller.exceptions;

import org.apache.tomcat.websocket.AuthenticationException;
import org.example.lab4.service.exceptions.EmailAlreadyExistsException;
import org.example.lab4.service.exceptions.ForbiddenException;
import org.example.lab4.service.exceptions.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;

import static org.example.lab4.controller.exceptions.ProblemDetailsUtils.getValidationErrorsProblemDetail;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail problemDetail = forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("user-not-found"));
        problemDetail.setTitle("User Not Found");
        return problemDetail;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    ProblemDetail handleEmailAlreadyExistException(EmailAlreadyExistsException ex) {
        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create("email-already-exists"));
        problemDetail.setTitle("Email Already Exists");
        return problemDetail;
    }

    @ExceptionHandler(ForbiddenException.class)
    ProblemDetail handleForbiddenException(ForbiddenException ex) {
        ProblemDetail problemDetail = forStatusAndDetail(FORBIDDEN, ex.getMessage());
        problemDetail.setType(URI.create("forbidden-exception"));
        problemDetail.setTitle("Forbidden exception");
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        ProblemDetail problemDetail = forStatusAndDetail(UNAUTHORIZED, ex.getMessage());
        problemDetail.setType(URI.create("authentication-exception"));
        problemDetail.setTitle("Authentication exception");
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        ProblemDetail problemDetail = forStatusAndDetail(UNAUTHORIZED, ex.getMessage());
        problemDetail.setType(URI.create("bad-credentials-exception"));
        problemDetail.setTitle("Bad credentials exception");
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        List<ParamsViolationDetails> validationResponse =
                errors.stream().map(err -> ParamsViolationDetails.builder().reason(err.getDefaultMessage()).fieldName(err.getField()).build()).toList();
        return ResponseEntity.status(BAD_REQUEST).body(getValidationErrorsProblemDetail(validationResponse));
    }
}
