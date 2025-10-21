
package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.entities.Business;

import java.util.Map;

/**
 * Interface de service pour l'intégration avec OpenAI
 */
public interface AIService {

    /**
     * Génère une description marketing pour une entreprise en utilisant OpenAI
     * @param business L'entreprise pour laquelle générer la description
     * @param prompt Consigne supplémentaire pour la génération
     * @return La description générée
     */
    String generateBusinessDescription(Business business, String prompt);

    /**
     * Analyse les commentaires des avis pour extraire des insights
     * @param businessId L'identifiant de l'entreprise
     * @return Une map contenant les insights extraits
     */
    Map<String, Object> analyzeReviewSentiment(Long businessId);

    /**
     * Génère des suggestions personnalisées pour améliorer l'entreprise
     * @param businessId L'identifiant de l'entreprise
     * @return Liste de suggestions sous forme de texte
     */
    String generateBusinessSuggestions(Long businessId);

    /**
     * Génère une réponse automatique à un avis client
     * @param reviewId L'identifiant de l'avis
     * @return La réponse générée
     */
    String generateReviewResponse(Long reviewId);
}