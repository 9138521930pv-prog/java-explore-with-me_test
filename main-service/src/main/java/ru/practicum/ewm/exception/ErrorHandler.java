package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.debug("Received 404 NOT_FOUND status: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .reason("The required object was not found.")
                .build();
    }


    @ExceptionHandler({MethodArgumentNotValidException.class, UncorrectedParametersException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerIncorrectParametersException(Exception e) {
        log.debug("Received 400 BAD_REQUEST status: {}", e.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .reason("Incorrect parameters")
                .build();
    }

    @ExceptionHandler({PSQLException.class, ConflictException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerValidationException(Exception e) {
        log.warn("Received 409 CONFLICT status: {}", e.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .reason("Request is CONFLICT")
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerOtherException(Throwable e) {
        log.error("Received 500 INTERNAL_SERVER_ERROR status: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .reason("Request is INTERNAL_SERVER_ERROR")
                .build();
    }
}