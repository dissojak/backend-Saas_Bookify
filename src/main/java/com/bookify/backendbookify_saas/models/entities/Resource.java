package com.bookify.backendbookify_saas.models.entities;

import com.bookify.backendbookify_saas.models.enums.ResourceStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une ressource qui peut être réservée
 */
@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatusEnum status;

    @Column(name = "price_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    @Column(length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ResourceAvailability> availabilities;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ResourceRating> ratings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private ResourceTemplate template;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ResourceAttribute> attributes = new ArrayList<>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ResourcePricingOption> pricingOptions = new ArrayList<>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ResourceImage> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "resource_staff",
            joinColumns = @JoinColumn(name = "resource_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    private List<Staff> assignedStaff = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void addAttribute(ResourceAttribute attribute) {
        attributes.add(attribute);
        attribute.setResource(this);
    }

    public void removeAttribute(ResourceAttribute attribute) {
        attributes.remove(attribute);
        attribute.setResource(null);
    }

    public void addPricingOption(ResourcePricingOption option) {
        pricingOptions.add(option);
        option.setResource(this);
    }

    public void removePricingOption(ResourcePricingOption option) {
        pricingOptions.remove(option);
        option.setResource(null);
    }

    public void addImage(ResourceImage image) {
        images.add(image);
        image.setResource(this);
    }

    public void removeImage(ResourceImage image) {
        images.remove(image);
        image.setResource(null);
    }

    public void assignStaff(Staff staff) {
        if (!assignedStaff.contains(staff)) {
            assignedStaff.add(staff);
        }
    }

    public void unassignStaff(Staff staff) {
        assignedStaff.remove(staff);
    }

    public ResourceImage getPrimaryImage() {
        return images.stream()
                .filter(ResourceImage::getIsPrimary)
                .findFirst()
                .orElse(null);
    }

    public ResourcePricingOption getDefaultPricingOption() {
        return pricingOptions.stream()
                .filter(ResourcePricingOption::getIsDefault)
                .findFirst()
                .orElse(null);
    }
}