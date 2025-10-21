package com.bookify.backendbookify_saas.models.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe représentant un propriétaire d'entreprise
 */
@Entity
@Table(name = "business_owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessOwner extends User {

    // Bidirectional one-to-one: Business is the owning side (holds owner_id FK)
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Business business;

    /**
     * Helper to set the business and keep both sides in sync.
     */
    public void assignBusiness(Business b) {
        this.business = b;
        if (b != null && b.getOwner() != this) {
            b.setOwner(this);
        }
    }

    /**
     * Helper to remove the association (owner no longer owns any business).
     * Note: business will keep its owner null — ensure you assign it to another owner afterwards.
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