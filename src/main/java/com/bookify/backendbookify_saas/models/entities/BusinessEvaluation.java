package com.bookify.backendbookify_saas.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "business_evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false, foreignKey = @ForeignKey(name = "fk_eval_business"))
    @JsonBackReference
    private Business business;

    @Column(name = "branding_score")
    private Integer brandingScore; // 0..100

    @Column(name = "name_professionalism_score")
    private Integer nameProfessionalismScore; // 0..100

    @Column(name = "email_professionalism_score")
    private Integer emailProfessionalismScore; // 0..100

    @Column(name = "description_professionalism_score")
    private Integer descriptionProfessionalismScore; // 0..100

    @Column(name = "location_score")
    private Integer locationScore; // 0..100

    @Column(name = "category_score")
    private Integer categoryScore; // 0..100 - how well the business matches the selected category

    @Column(name = "overall_score")
    private Integer overallScore; // 0..100

    @Column(name = "name_details", length = 1000)
    private String nameDetails;

    @Column(name = "email_details", length = 1000)
    private String emailDetails;

    @Column(name = "description_details", length = 1000)
    private String descriptionDetails;

    @Column(name = "branding_details", length = 1000)
    private String brandingDetails;

    @Column(name = "location_details", length = 1000)
    private String locationDetails;

    @Column(name = "category_details", length = 1000)
    private String categoryDetails;

    @Column(name = "name_suggestions", length = 1000)
    private String nameSuggestions;

    @Column(name = "email_suggestions", length = 1000)
    private String emailSuggestions;

    @Column(name = "description_suggestions", length = 1000)
    private String descriptionSuggestions;

    @Column(name = "branding_suggestions", length = 1000)
    private String brandingSuggestions;

    @Column(name = "source", length = 50)
    private String source; // ex: "AI" or "HEURISTIC"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
