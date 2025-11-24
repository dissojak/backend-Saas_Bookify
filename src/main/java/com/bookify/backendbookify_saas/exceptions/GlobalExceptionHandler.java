package com.bookify.backendbookify_saas.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestionnaire global des exceptions pour l'API
 * Centralise la gestion des erreurs pour toute l'application
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions UserAlreadyExistsException
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions InvalidTokenException
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidToken(
            InvalidTokenException ex,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions HttpRequestMethodNotSupportedException (méthodes HTTP non autorisées)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            WebRequest request) {
        String supportedMethods = ex.getSupportedHttpMethods() != null
                ? ex.getSupportedHttpMethods().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "))
                : "N/A";

        String message = String.format(
                "Méthode HTTP non autorisée pour cette URL. Utilisez plutôt : %s",
                supportedMethods
        );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("message", message));
    }

    /**
     * Gère les exceptions MethodNotAllowedException personnalisées
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<Map<String, String>> handleCustomMethodNotAllowed(
            MethodNotAllowedException ex,
            WebRequest request) {
        String allowedMethods = ex.getAllowedMethods().isEmpty()
                ? "Aucune"
                : ex.getAllowedMethods().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

        String message = ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : String.format("Méthode HTTP non autorisée pour cette URL. Utilisez plutôt : %s", allowedMethods);

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("message", message));
    }

    /**
     * Gère les erreurs de validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("message", "Erreur de validation");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        errors.put("errors", fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    /**
     * Handles Spring Security authentication exceptions (invalid credentials)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {
        String message = ex instanceof BadCredentialsException
                ? "Invalid email or password"
                : ex.getMessage();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", message));
    }

    /**
     * Handles unauthorized access exceptions (not owner of resource)
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedAccess(
            UnauthorizedAccessException ex,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère le cas où un utilisateur est déjà membre du personnel d'une autre entreprise
     */
    @ExceptionHandler(UserAlreadyStaffException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyStaff(
            UserAlreadyStaffException ex,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère toutes les autres exceptions non gérées
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(
            Exception ex,
            WebRequest request) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "Une erreur interne est survenue",
                        "error", ex.getClass().getSimpleName(),
                        "detail", ex.getMessage() != null ? ex.getMessage() : "No details"
                ));
    }

    /**
     * Gère les exceptions HttpMediaTypeNotSupportedException (types de médias HTTP non supportés)
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            WebRequest request) {
        String supportedMediaTypes = ex.getSupportedMediaTypes() != null
                ? ex.getSupportedMediaTypes().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "))
                : "N/A";

        String message = String.format(
                "Type de média HTTP non supporté pour cette URL. Utilisez plutôt : %s",
                supportedMediaTypes
        );

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(Map.of("message", message));
    }

    /**
     * Gère les exceptions HttpMessageNotReadableException (corps de requête HTTP illisible)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Corps de requête illisible"));
    }
}
