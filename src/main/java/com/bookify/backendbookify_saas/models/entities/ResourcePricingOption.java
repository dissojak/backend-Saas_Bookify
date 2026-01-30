package com.bookify.backendbookify_saas.models.entities;

import com.bookify.backendbookify_saas.models.enums.ResourcePricingTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ResourcePricingOption entity - defines flexible pricing for resources
 * A resource can have multiple pricing options: hourly, daily, weekly, per-person, etc.
 * Example: car rental with "Per Day: $50", "Per Week: $300", "Per Month: $1200"
 */
@Entity
@Table(name = "resource_pricing_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourcePricingOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourcePricingTypeEnum pricingType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 255)
    private String label;

    @Column
    private Integer minDuration;

    @Column
    private Integer maxDuration;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

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
