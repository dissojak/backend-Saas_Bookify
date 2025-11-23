package com.bookify.backendbookify_saas.email_SMTP;

/**
 * Interface pour le service d'envoi d'emails
 */
public interface MailService {

    /**
     * Envoie un email d'activation de compte
     * @param recipientEmail Email du destinataire
     * @param recipientName Nom du destinataire
     * @param activationToken Token d'activation
     */
    void sendActivationEmail(String recipientEmail, String recipientName, String activationToken);

    /**
     * Envoie un email de confirmation d'activation
     * @param recipientEmail Email du destinataire
     * @param recipientName Nom du destinataire
     */
    void sendActivationConfirmationEmail(String recipientEmail, String recipientName);

    /**
     * Envoie un email pour informer de la suppression du compte
     * @param recipientEmail Email du destinataire
     * @param recipientName Nom du destinataire
     */
    void sendAccountDeletionEmail(String recipientEmail, String recipientName);

    /**
     * Envoie un email simple avec sujet et contenu personnalisés
     * @param to Email du destinataire
     * @param subject Sujet de l'email
     * @param text Contenu de l'email
     */
    void sendSimpleMessage(String to, String subject, String text);
}
