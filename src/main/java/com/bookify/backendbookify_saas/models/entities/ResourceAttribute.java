package com.bookify.backendbookify_saas.models.entities;

import com.bookify.backendbookify_saas.models.enums.AttributeTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ResourceAttribute entity - stores actual attribute values for a resource
 * Can be generated from a template or added manually
 * Example: resource_id=1, attribute_key="brand", attribute_value="Tesla"
 */
@Entity
@Table(name = "resource_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String attributeKey;

    @Column(nullable = false, length = 1000)
    private String attributeValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeTypeEnum attributeType;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
