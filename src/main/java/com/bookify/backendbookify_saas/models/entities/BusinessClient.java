package com.bookify.backendbookify_saas.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a client that belongs to a specific business.
 * This is different from a global User - it's a business-specific contact.
 */
@Entity
@Table(name = "business_clients",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_business_client_phone",
        columnNames = {"business_id", "phone"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column
    private String email;

    @Column(length = 2000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_business_client_business"))
    @JsonBackReference
    private Business business;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

