package com.bookify.backendbookify_saas.schedulers;

import com.bookify.backendbookify_saas.email_SMTP.MailService;
import com.bookify.backendbookify_saas.models.entities.ActivationToken;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.repositories.ActivationTokenRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tâche planifiée pour nettoyer les tokens d'activation expirés
 * S'exécute tous les jours à 19h00
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivationTokenCleanupScheduler {

    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    /**
     * Nettoie les tokens d'activation expirés tous les jours à 19h00
     * Supprime les comptes non activés et envoie un email d'information
     *
     * Cron expression: "0 0 0 * * *" = tous les jours à 00h00 ( mid-night )
     * Format: seconde minute heure jour mois jour-de-la-semaine
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredActivationTokens() {
        log.info("Démarrage du nettoyage des tokens d'activation expirés à {}", LocalDateTime.now());

        try {
            // 1. Récupérer tous les tokens d'activation de la base de données
            List<ActivationToken> allTokens = activationTokenRepository.findAll();

            int deletedTokens = 0;
            int deletedUsers = 0;

            // 2. Parcourir tous les tokens pour trouver ceux qui sont expirés
            for (ActivationToken token : allTokens) {
                if (token.isExpired()) {
                    User user = token.getUser();
                    String userEmail = user.getEmail();
                    String userName = user.getName();

                    log.info("Token expiré trouvé pour l'utilisateur: {} ({})", userName, userEmail);

                    // 3. Envoyer un email d'information au client
                    try {
                        mailService.sendAccountDeletionEmail(userEmail, userName);
                        log.info("Email de notification envoyé à: {}", userEmail);
                    } catch (Exception e) {
                        log.error("Erreur lors de l'envoi de l'email à {}: {}", userEmail, e.getMessage());
                        // Continue même si l'email échoue
                    }

                    // 4. Supprimer le token d'activation
                    activationTokenRepository.delete(token);
                    deletedTokens++;
                    log.info("Token d'activation supprimé pour: {}", userEmail);

                    // 5. Supprimer le compte utilisateur non activé
                    userRepository.delete(user);
                    deletedUsers++;
                    log.info("Compte utilisateur supprimé: {} ({})", userName, userEmail);
                }
            }

            log.info("Nettoyage terminé: {} tokens expirés supprimés, {} comptes utilisateurs supprimés",
                     deletedTokens, deletedUsers);

        } catch (Exception e) {
            log.error("Erreur lors du nettoyage des tokens d'activation expirés: {}", e.getMessage(), e);
        }
    }

    /**
     * Méthode alternative avec intervalle fixe (toutes les 24 heures)
     * Peut être utilisée si la planification cron ne fonctionne pas
     * Cette méthode est commentée par défaut
     */
    // @Scheduled(fixedRate = 86400000) // 24 heures en millisecondes
    // @Transactional
    // public void cleanupExpiredActivationTokensWithFixedRate() {
    //     cleanupExpiredActivationTokens();
    // }
}

