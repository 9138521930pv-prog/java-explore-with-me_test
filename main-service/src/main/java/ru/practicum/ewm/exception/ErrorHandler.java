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

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.debug("Received 404 NOT_FOUND status: {}", e.getMessage(), e);
        return buildApiError(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                "The required object was not found.",
                List.of(e.getMessage())
        );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            UncorrectedParametersException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerIncorrectParametersException(Exception e) {
        log.debug("Received 400 BAD_REQUEST status: {}", e.getMessage(), e);

        List<String> errors = extractErrors(e);
        String reason = determineReason(e);

        return buildApiError(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                reason,
                errors
        );
    }

    @ExceptionHandler({
            PSQLException.class,
            ConflictException.class,
            DataIntegrityViolationException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlerConflictException(Exception e) {
        log.warn("Received 409 CONFLICT status: {}", e.getMessage(), e);

        String errorMessage;
        if (e instanceof PSQLException psqlEx) {
            errorMessage = "Database conflict: " + psqlEx.getServerErrorMessage().getMessage();
        } else {
            errorMessage = e.getMessage();
        }

        return buildApiError(
                HttpStatus.CONFLICT,
                errorMessage,
                "Request is CONFLICT",
                List.of(errorMessage)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handlerOtherException(Throwable e) {
        log.error("Received 500 INTERNAL_SERVER_ERROR status: {}", e.getMessage(), e);

        String summary = e.getClass().getSimpleName() + ": " + e.getMessage();

        return buildApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal server error occurred",
                "Request is INTERNAL_SERVER_ERROR",
                List.of(summary)
        );
    }

    private ApiError buildApiError(HttpStatus status, String message, String reason, List<String> errors) {
        return ApiError.builder()
                .status(status.value())
                .message(message)
                .reason(reason)
                .errors(errors)
                .build();
    }

    private List<String> extractErrors(Exception e) {
        if (e instanceof MethodArgumentNotValidException validationEx) {
            return validationEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(f -> f.getField() + ": " + f.getDefaultMessage())
                    .collect(Collectors.toList());
        }

        if (e instanceof UncorrectedParametersException) {
            return List.of(e.getMessage());
        }

        if (e instanceof MethodArgumentTypeMismatchException typeEx) {
            return List.of("Parameter '" + typeEx.getName() + "' has invalid type: " + typeEx.getRequiredType().getSimpleName());
        }

        if (e instanceof MissingServletRequestParameterException missingEx) {
            return List.of("Missing required parameter: " + missingEx.getParameterName());
        }

        return List.of(e.getMessage());
    }

    private String determineReason(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            return "Validation failed";
        }
        if (e instanceof UncorrectedParametersException) {
            return "Incorrect parameters";
        }
        if (e instanceof MethodArgumentTypeMismatchException) {
            return "Parameter type mismatch";
        }
        if (e instanceof MissingServletRequestParameterException) {
            return "Missing required parameter";
        }
        return "Incorrect parameters";
    }
}
