package com.bookify.backendbookify_saas.services;

public interface BusinessProfile {
    String getName();
    String getLocation();
    String getCategoryName();
    String getPhone();
    String getEmail();
    String getDescription(); // peut être vide si non disponible sur l'entité
    int getImageCount();     // number of business images
}

