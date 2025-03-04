package org.skyline.mcq.infrastructure.http;

import lombok.AllArgsConstructor;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.dto.ErrorResponse;
import org.skyline.mcq.infrastructure.http.dto.ExceptionModel;
import org.skyline.mcq.infrastructure.http.dto.Error;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)   // Ruta
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
