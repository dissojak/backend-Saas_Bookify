package com.bookify.backendbookify_saas.models.entities;

import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.bookify.backendbookify_saas.models.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing all users in the system.
 * Users are differentiated by their role (CLIENT, BUSINESS_OWNER, ADMIN, STAFF).
 * Staff extends this class for additional staff-specific attributes.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatusEnum status;

    // Map avatarUrl to a large text column so long URLs don't get truncated by default VARCHAR(255)
    @Lob
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires_at")
    private LocalDateTime passwordResetExpiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Relationships for users with CLIENT role
    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ServiceRating> serviceRatings = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BusinessRating> businessRatings = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ResourceRating> resourceRatings = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ResourceReservation> resourceReservations = new ArrayList<>();

    // Relationship for users with BUSINESS_OWNER role
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Business business;

    /**
     * Helper to set the business and keep both sides in sync.
     * Only applicable for users with BUSINESS_OWNER role.
     */
    public void assignBusiness(Business b) {
        this.business = b;
        if (b != null && b.getOwner() != this) {
            b.setOwner(this);
        }
    }

    /**
     * Helper to remove the business association.
     * Only applicable for users with BUSINESS_OWNER role.
     */
    public void removeBusiness() {
        if (this.business != null) {
            Business prev = this.business;
            this.business = null;
            if (prev.getOwner() == this) {
                prev.setOwner(null);
            }
        }
    }

}
