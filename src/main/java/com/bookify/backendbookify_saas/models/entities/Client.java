package com.bookify.backendbookify_saas.models.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un client qui utilise le système
 */
@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client extends User {

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

//    // helper methods to keep both sides in sync
//    public void addServiceRating(ServiceRating r) {
//        serviceRatings.add(r);
//        r.setClient(this);
//    }
//    public void removeServiceRating(ServiceRating r) {
//        serviceRatings.remove(r);
//        r.setClient(null);
//    }
//
//    public void addBusinessRating(BusinessRating r) {
//        businessRatings.add(r);
//        r.setClient(this);
//    }
//    public void removeBusinessRating(BusinessRating r) {
//        businessRatings.remove(r);
//        r.setClient(null);
//    }
//
//    public void addResourceRating(ResourceRating r) {
//        resourceRatings.add(r);
//        r.setClient(this);
//    }
//    public void removeResourceRating(ResourceRating r) {
//        resourceRatings.remove(r);
//        r.setClient(null);
//    }
//
//    public void addResourceReservation(ResourceReservation rr) {
//        resourceReservations.add(rr);
//        rr.setClient(this);
//    }
//    public void removeResourceReservation(ResourceReservation rr) {
//        resourceReservations.remove(rr);
//        rr.setClient(null);
//    }

}