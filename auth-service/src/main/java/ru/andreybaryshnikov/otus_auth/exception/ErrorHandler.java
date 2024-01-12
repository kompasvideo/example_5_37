package ru.andreybaryshnikov.otus_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorizedException(final UnauthorizedException e) {
        return "UNAUTHORIZED";
    }
}
