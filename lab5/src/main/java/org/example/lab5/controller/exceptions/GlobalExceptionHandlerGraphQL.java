package org.example.lab5.controller.exceptions;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import org.apache.tomcat.websocket.AuthenticationException;
import org.example.lab5.service.exceptions.EmailAlreadyExistsException;
import org.example.lab5.service.exceptions.ForbiddenException;
import org.example.lab5.service.exceptions.UserNotFoundException;
import org.example.lab5.service.exceptions.UsernameAlreadyExistsException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandlerGraphQL extends ResponseEntityExceptionHandler {

    @GraphQlExceptionHandler(UserNotFoundException.class)
    GraphQLError handleUserNotFound(UserNotFoundException ex) {
        return GraphQLError.newError().message(ex.getMessage())
                .errorType(ErrorClassification.errorClassification("user-not-found"))
                .build();

    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    GraphQLError handleEmailAlreadyExistException(EmailAlreadyExistsException ex) {
        return GraphQLError.newError().message(ex.getMessage())
                .errorType(ErrorClassification.errorClassification("email-already-exists"))
                .build();
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    GraphQLError handleUsernameAlreadyExistException(UsernameAlreadyExistsException ex) {
        return GraphQLError.newError().message(ex.getMessage())
                .errorType(ErrorClassification.errorClassification("username-already-exists"))
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    GraphQLError handleForbiddenException(ForbiddenException ex) {
        return GraphQLError.newError().message(ex.getMessage())
                .errorType(ErrorClassification.errorClassification("forbidden-exception"))
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    GraphQLError handleAuthenticationException(AuthenticationException ex) {
        return GraphQLError.newError().message(ex.getMessage())
                .errorType(ErrorClassification.errorClassification("authentication-exception"))
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    GraphQLError handleBadCredentialsException(BadCredentialsException ex) {
        return GraphQLError.newError().message(ex.getMessage())
                .errorType(ErrorClassification.errorClassification("bad-credentials-exception"))
                .build();
    }
}
