package com.bookify.backendbookify_saas.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ResourceTemplate entity - allows businesses to define reusable attribute schemas
 * Example: "Car" template with attributes like brand, horsepower, fuel consumption
 *          "Football Field" template with attributes like surface type, capacity
 */
@Entity
@Table(name = "resource_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TemplateAttribute> attributes = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addAttribute(TemplateAttribute attribute) {
        attributes.add(attribute);
        attribute.setTemplate(this);
    }

    public void removeAttribute(TemplateAttribute attribute) {
        attributes.remove(attribute);
        attribute.setTemplate(null);
    }
}
