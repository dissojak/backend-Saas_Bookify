package com.bookify.backendbookify_saas.exceptions;

/**
 * Exception levée lorsqu'un utilisateur existe déjà
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

