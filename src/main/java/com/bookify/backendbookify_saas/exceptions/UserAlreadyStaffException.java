package com.bookify.backendbookify_saas.exceptions;

/**
 * Exception levée lorsqu'un utilisateur est déjà membre du personnel d'une autre entreprise.
 */
public class UserAlreadyStaffException extends RuntimeException {
    public UserAlreadyStaffException(String message) {
        super(message);
    }
}

