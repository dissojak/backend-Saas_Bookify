package com.bookify.backendbookify_saas.models.entities;

import com.bookify.backendbookify_saas.models.enums.AttributeTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TemplateAttribute entity - defines a single attribute in a template schema
 * Example: attribute_key="brand", attribute_type=TEXT, required=true
 */
@Entity
@Table(name = "template_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String attributeKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeTypeEnum attributeType;

    @Column(nullable = false)
    private Boolean required;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ResourceTemplate template;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
