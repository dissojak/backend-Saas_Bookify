package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessEvaluationResponse {
    private Long id;

    private Integer brandingScore;
    private Integer nameProfessionalismScore;
    private Integer emailProfessionalismScore;
    private Integer descriptionProfessionalismScore;
    private Integer locationScore;
    private Integer categoryScore;
    private Integer overallScore;

    private String nameDetails;
    private String emailDetails;
    private String descriptionDetails;
    private String brandingDetails;
    private String locationDetails;
    private String categoryDetails;

    private String nameSuggestions;
    private String emailSuggestions;
    private String descriptionSuggestions;
    private String brandingSuggestions;

    private String source;
    private LocalDateTime createdAt;
}

