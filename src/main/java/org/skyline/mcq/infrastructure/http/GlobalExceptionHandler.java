package org.skyline.mcq.infrastructure.http;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import org.skyline.mcq.domain.exceptions.*;
import org.skyline.mcq.infrastructure.http.dto.Error;
import org.skyline.mcq.infrastructure.http.dto.ErrorResponse;
import org.skyline.mcq.infrastructure.http.dto.ExceptionModel;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult result =  ex.getBindingResult();

        List<Error> errors = new ArrayList<>();
        result.getFieldErrors().forEach( error -> {
            String message = messageSource.getMessage( error, Locale.forLanguageTag( "US" ));
            errors.add( new Error(message, error.getField(), "body"));
        });
        ExceptionModel<List<Error>> responseBody = new ExceptionModel<>(new Date().toString(), status.value(), status, errors);

        return new ResponseEntity<>(responseBody, status);
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse<String>> handleAccountAlreadyExistsException(AccountAlreadyExistsException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse<String>> handleNotFoundException(NotFoundException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse<String>> handleConflictException(ConflictException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse<String>> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ErrorResponse<String>> handleTokenOperationException(TokenGenerationException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(TokenOperationException.class)
    public ResponseEntity<ErrorResponse<String>> handleTokenOperationException(TokenOperationException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse<String>> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse<String>> handleSignatureException(SignatureException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse<String>> handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse<String>> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse<String>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse<String>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        ErrorResponse<String> errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.toString(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    private <T> ErrorResponse<T> buildErrorResponse(int status, String httpError, T message, String path) {
        return new ErrorResponse<>(
                LocalDateTime.now(),
                status,
                httpError,
                message,
                path
        );
    }
}
