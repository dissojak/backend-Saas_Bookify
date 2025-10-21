package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.services.BusinessProfile;

public class BusinessProfileAdapter implements BusinessProfile {

    private final Business b;

    public BusinessProfileAdapter(Business business) {
        this.b = business;
    }

    @Override
    public String getName() {
        return nz(b.getName());
    }

    @Override
    public String getLocation() {
        return nz(b.getLocation());
    }

    @Override
    public String getCategoryName() {
        return b.getCategory() != null ? nz(b.getCategory().getName()) : "";
    }

    @Override
    public String getPhone() {
        return nz(b.getPhone());
    }

    @Override
    public String getEmail() {
        return nz(b.getEmail());
    }

    @Override
    public String getDescription() {
        // L’entité Business n’a pas de champ description; on en dérive une basique.
        String base = (b.getCategory()!=null? nz(b.getCategory().getName()):"") + " " + nz(b.getLocation());
        return base.trim();
    }

    private static String nz(String s) { return s == null ? "" : s; }
}

