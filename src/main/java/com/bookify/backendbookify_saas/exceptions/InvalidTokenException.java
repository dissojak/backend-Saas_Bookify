package com.bookify.backendbookify_saas.exceptions;

/**
 * Exception levée lorsqu'un token d'activation est invalide ou expiré
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}

