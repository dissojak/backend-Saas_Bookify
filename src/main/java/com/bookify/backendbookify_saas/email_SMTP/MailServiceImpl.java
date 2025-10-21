package com.bookify.backendbookify_saas.email_SMTP;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implementation of email service with HTML templates and friendly sender name.
 * Updated to send a cleaner activation email that matches the website UI and
 * shows only the activation button (no visible activation code).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Friendly display name shown to recipients (e.g. "Bookify <no-reply@...>")
    @Value("${application.email.from-display-name:Bookify}")
    private String fromDisplayName;

    @Value("${application.base-url:http://localhost:8088}")
    private String baseUrl;

    @Override
    public void sendActivationEmail(String recipientEmail, String recipientName, String activationToken) {
        String activationLink = baseUrl + "/v1/auth/activate?token=" + activationToken;
        String subject = "Activate your Bookify account";

        // Build HTML that matches front-end style: logo, headline, friendly text and single CTA button.
        String html = buildActivationHtml(recipientName, activationLink);
        // Plain text fallback still includes the activation link (no code displayed).
        String plain = String.format(
                "Hello %s,\n\nActivate your Bookify account by visiting the link below:\n\n%s\n\nIf you did not sign up, ignore this email.\n",
                recipientName, activationLink
        );

        sendHtmlEmail(recipientEmail, subject, plain, html);
    }

    @Override
    public void sendActivationConfirmationEmail(String recipientEmail, String recipientName) {
        String subject = "Your Bookify account is activated";
        String html = "<div style=\"font-family:system-ui,Arial,sans-serif;max-width:520px;padding:20px\">" +
                "<h2 style=\"color:#0d9488;margin:0 0 12px;font-size:20px;\">Welcome to Bookify</h2>" +
                "<p>Hi " + safe(recipientName) + ",</p>" +
                "<p>Your account has been activated successfully. You can now log in and start using Bookify.</p>" +
                "<p style=\"color:#6b7280;font-size:13px;margin-top:18px;\">If you did not request this, please contact support.</p>" +
                "</div>";
        String plain = String.format("Hello %s,\n\nYour account has been activated. You can now log in.\n", recipientName);

        sendHtmlEmail(recipientEmail, subject, plain, html);
    }

    @Override
    public void sendAccountDeletionEmail(String recipientEmail, String recipientName) {
        String subject = "Your Bookify account has been deleted";
        String accent = "#ef4444"; // Red color for deletion notice

        String html = "<div style=\"font-family:system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; color:#111827;\">" +
                "<div style=\"max-width:520px;margin:0 auto;padding:28px;background:#ffffff;border-radius:10px;box-shadow:0 4px 18px rgba(15,23,42,0.06);\">" +

                // Logo
                "<div style=\"text-align:left;margin-bottom:18px;\">" +
                "<img src=\"" + baseUrl + "/assets/logo-compact.png\" alt=\"Bookify\" style=\"height:34px;display:inline-block;vertical-align:middle;\" onerror=\"this.style.display='none'\" />" +
                "</div>" +

                // Headline
                "<h2 style=\"color:" + accent + ";margin:0 0 8px;font-size:20px;\">Account Deletion Notice</h2>" +

                // Greeting and message
                "<p style=\"margin:0 0 16px;font-size:15px;color:#374151;\">Hi " + safe(recipientName) + ",</p>" +
                "<p style=\"margin:0 0 16px;font-size:15px;color:#374151;\">" +
                "Your Bookify account has been automatically deleted because the activation link expired without being used." +
                "</p>" +

                "<p style=\"margin:0 0 16px;font-size:15px;color:#374151;\">" +
                "Activation tokens are valid for <strong>7 days</strong> from the registration date." +
                "</p>" +

                // Info box
                "<div style=\"background:#fef2f2;border-left:4px solid " + accent + ";padding:12px 16px;margin:0 0 22px;border-radius:4px;\">" +
                "<p style=\"margin:0;font-size:14px;color:#991b1b;\"><strong>What does this mean?</strong></p>" +
                "<p style=\"margin:8px 0 0;font-size:14px;color:#991b1b;\">All your account data has been permanently removed from our system.</p>" +
                "</div>" +

                // CTA section
                "<p style=\"margin:0 0 8px;font-size:15px;color:#374151;\">If you still want to use Bookify, you can create a new account:</p>" +
                "<p style=\"margin:0 0 22px;\">" +
                "<a href=\"" + baseUrl + "/v1/auth/signup\" target=\"_blank\" rel=\"noopener noreferrer\" " +
                "style=\"background:#0d9488;color:#ffffff;padding:12px 20px;text-decoration:none;border-radius:10px;font-weight:700;display:inline-block;\">Sign up again</a>" +
                "</p>" +

                // Footer
                "<hr style=\"border:none;border-top:1px solid #e6e9ee;margin:22px 0;\" />" +
                "<p style=\"font-size:12px;color:#9ca3af;margin:0;\">This is an automated message. If you have questions, please contact our support team.</p>" +
                "</div>" +

                // Copyright
                "<div style=\"max-width:520px;margin:12px auto 0;text-align:center;font-size:12px;color:#9ca3af;\">" +
                "© " + java.time.Year.now().getValue() + " Bookify. All rights reserved." +
                "</div>" +
                "</div>";

        String plain = String.format(
                "Hello %s,\n\n" +
                        "Your Bookify account has been automatically deleted because the activation link expired without being used.\n\n" +
                        "Activation tokens are valid for 7 days from the registration date.\n\n" +
                        "If you still want to use Bookify, you can create a new account at: %s/v1/auth/signup\n\n" +
                        "Best regards,\n" +
                        "The Bookify Team",
                recipientName,
                baseUrl
        );

        try {
            sendHtmlEmail(recipientEmail, subject, plain, html);
            log.info("Email de suppression de compte envoyé à : {}", recipientEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de suppression à {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email de suppression", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String plainText, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            // set friendly from (display name) without exposing internal system address
            helper.setFrom(new InternetAddress(fromEmail, fromDisplayName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainText, html);

            mailSender.send(msg);
            log.info("Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to build/send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Build a clean activation email HTML that focuses on the primary CTA button and matches
     * the front-end look (accent color, logo, and simple content). No activation code is shown.
     *
     * Notes:
     * - Emails must use inline CSS for best compatibility.
     * - If you host a logo on the frontend, ensure baseUrl + "/assets/logo.png" (or similar) resolves publicly.
     */
    private String buildActivationHtml(String name, String activationLink) {
        String safeName = safe(name);
        // Accent color matches the site (#0d9488). Adjust if your frontend uses a different variable.
        String accent = "#0d9488";

        // Small preheader (hidden) to improve inbox snippet
        String preheader = "Activate your Bookify account to get started.";

        return "<div style=\"font-family:system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; color:#111827;\">" +
                // Hidden preheader
                "<div style=\"display:none;max-height:0px;overflow:hidden;color:transparent;opacity:0;\">" + preheader + "</div>" +

                // Container
                "<div style=\"max-width:520px;margin:0 auto;padding:28px;background:#ffffff;border-radius:10px;box-shadow:0 4px 18px rgba(15,23,42,0.06);\">" +

                // Logo (optional) - point to a public asset on your frontend
                "<div style=\"text-align:left;margin-bottom:18px;\">" +
                "<img src=\"" + baseUrl + "/assets/logo-compact.png\" alt=\"Bookify\" style=\"height:34px;display:inline-block;vertical-align:middle;\" onerror=\"this.style.display='none'\" />" +
                "</div>" +

                // Headline
                "<h2 style=\"color:" + accent + ";margin:0 0 8px;font-size:20px;\">Welcome to Bookify</h2>" +

                // Greeting and message
                "<p style=\"margin:0 0 16px;font-size:15px;color:#374151;\">Hi " + safeName + ",</p>" +
                "<p style=\"margin:0 0 22px;font-size:15px;color:#374151;\">Click the button below to activate your account and start exploring Bookify.</p>" +

                // CTA button (primary)
                "<p style=\"margin:0 0 22px;\">" +
                "<a href=\"" + activationLink + "\" target=\"_blank\" rel=\"noopener noreferrer\" " +
                "style=\"background:" + accent + ";color:#ffffff;padding:12px 20px;text-decoration:none;border-radius:10px;font-weight:700;display:inline-block;\">Activate account</a>" +
                "</p>" +

                // Small note
                "<p style=\"margin:0 0 0;font-size:13px;color:#6b7280;\">If the button doesn't work, copy and paste the following link into your browser:</p>" +
                "<p style=\"word-break:break-all;font-size:12px;color:#6b7280;margin-top:6px;\">" +
                "<a href=\"" + activationLink + "\" style=\"color:#6b7280;text-decoration:underline;\">" + activationLink + "</a>" +
                "</p>" +

                // Footer
                "<hr style=\"border:none;border-top:1px solid #e6e9ee;margin:22px 0;\" />" +
                "<p style=\"font-size:12px;color:#9ca3af;margin:0;\">If you did not sign up for a Bookify account, you can safely ignore this email.</p>" +
                "</div>" + // container end

                // Page background (subtle)
                "<div style=\"max-width:520px;margin:12px auto 0;text-align:center;font-size:12px;color:#9ca3af;\">" +
                "© " + java.time.Year.now().getValue() + " Bookify. All rights reserved." +
                "</div>" +
                "</div>";
    }

    // simple HTML-escape for names (keeps snippet small)
    private String safe(String s) {
        return s == null ? "" : s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}