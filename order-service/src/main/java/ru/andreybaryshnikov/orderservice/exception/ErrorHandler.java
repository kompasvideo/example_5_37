package ru.andreybaryshnikov.orderservice.exception;

import jakarta.servlet.http.HttpServletResponse;
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConflictException(final ConflictException e) {
        return "ConflictException";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(final BadRequestException e) {
        return "BadRequestException";
    }

    @ExceptionHandler
    public String handleTheProductIsOutOfStockException (final TheProductIsOutOfStock e, HttpServletResponse response)
    {
        response.setStatus(450);
        return "TheProductIsOutOfStock - " + e;
    }
}
