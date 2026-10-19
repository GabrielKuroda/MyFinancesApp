package com.dev.myfinance.exceptions.handler;

import com.dev.myfinance.exceptions.BusinessValidationException;
import com.dev.myfinance.exceptions.CustomNotFoundException;
import com.dev.myfinance.exceptions.response.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice(basePackages = "com.dev.myfinance.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handlerNotFoundExceptions(
            CustomNotFoundException ex,
            HttpServletRequest request
    ) {
        CustomErrorResponse errorResponse = CustomErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String detailedMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("'%s': %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        CustomErrorResponse errorResponse = CustomErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                detailedMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<CustomErrorResponse> handleBusinessValidationException(
            BusinessValidationException ex,
            HttpServletRequest request
    ) {
        String detail = String.format("'%s': %s", ex.getField(), ex.getMessage());

        CustomErrorResponse errorResponse = CustomErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                detail,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {
        CustomErrorResponse errorResponse = CustomErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro interno inesperado no servidor.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
