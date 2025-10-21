package com.bookify.backendbookify_saas.models.entities;

import com.bookify.backendbookify_saas.models.enums.BusinessStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "businesses",
        uniqueConstraints = @UniqueConstraint(name = "uq_business_owner", columnNames = "owner_id"))
@EntityListeners(com.bookify.backendbookify_saas.models.entities.listeners.BusinessEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusinessStatus status = BusinessStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_business_owner"))
    @JsonBackReference
    private BusinessOwner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_business_category"))
    private Category category;

    // Collections initialized to avoid NPEs; LAZY fetch to avoid accidental loads
    @OneToMany(mappedBy = "business", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<com.bookify.backendbookify_saas.models.entities.Service> services = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Staff> staff = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Resource> resources = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BusinessRating> ratings = new ArrayList<>();

    // helper methods
    public void addService(Service s) {
        services.add(s);
        s.setBusiness(this);
    }
    public void removeService(Service s) {
        services.remove(s);
        s.setBusiness(null);
    }
    public void addStaff(Staff st) {
        staff.add(st);
        st.setBusiness(this);
    }
    public void removeStaff(Staff st) {
        staff.remove(st);
        st.setBusiness(null);
    }
    public void addResource(Resource r) {
        resources.add(r);
        r.setBusiness(this);
    }
    public void removeResource(Resource r) {
        resources.remove(r);
        r.setBusiness(null);
    }
    public void addRating(BusinessRating br) {
        ratings.add(br);
        br.setBusiness(this);
    }
    public void removeRating(BusinessRating br) {
        ratings.remove(br);
        br.setBusiness(null);
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = BusinessStatus.DRAFT;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}