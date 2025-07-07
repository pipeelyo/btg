package com.example.btg.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        
        // Obtener todos los errores de validación
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        body.put("message", "Error de validación: " + errors);
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    @ExceptionHandler({FondoNoEncontradoException.class, SaldoInsuficienteException.class, RuntimeException.class})
    public ResponseEntity<Object> handleCustomExceptions(RuntimeException ex, WebRequest request) {
        HttpStatus status = ex instanceof FondoNoEncontradoException ? HttpStatus.NOT_FOUND : 
                          ex instanceof SaldoInsuficienteException ? HttpStatus.BAD_REQUEST : 
                          HttpStatus.INTERNAL_SERVER_ERROR;
        
        return buildErrorResponse(ex, status, ex.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        
        return new ResponseEntity<>(body, status);
    }
}
