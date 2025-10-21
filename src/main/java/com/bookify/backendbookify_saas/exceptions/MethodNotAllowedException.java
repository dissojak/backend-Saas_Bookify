package com.bookify.backendbookify_saas.exceptions;

import org.springframework.http.HttpMethod;

import java.util.Set;

/**
 * Exception levée lorsqu'une méthode HTTP non autorisée est utilisée
 */
public class MethodNotAllowedException extends RuntimeException {

    private final Set<HttpMethod> allowedMethods;

    public MethodNotAllowedException(String message) {
        super(message);
        this.allowedMethods = Set.of();
    }

    public MethodNotAllowedException(String message, Set<HttpMethod> allowedMethods) {
        super(message);
        this.allowedMethods = allowedMethods;
    }

    public Set<HttpMethod> getAllowedMethods() {
        return allowedMethods;
    }
}

