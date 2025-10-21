package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.entities.Booking;
import com.bookify.backendbookify_saas.models.entities.User;

/**
 * Interface de service pour l'envoi d'emails
 */
public interface EmailService {

    void sendVerificationEmail(User user, String verificationToken);

    void sendPasswordResetEmail(User user, String resetToken);

    void sendBookingConfirmation(Booking booking);

    void sendBookingReminder(Booking booking);

    void sendBookingCancellation(Booking booking, String reason);

    void sendCustomEmail(String to, String subject, String content);

    void sendTemplatedEmail(String to, String subject, String templateName, Object model);
}
