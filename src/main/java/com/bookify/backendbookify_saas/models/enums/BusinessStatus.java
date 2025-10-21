package com.bookify.backendbookify_saas.models.enums;

public enum BusinessStatus {
    DRAFT,          // created by owner, not public
    PENDING,        // waiting approval (review flow with the AI)
    ACTIVE,         // visible and usable
    SUSPENDED,      // temporarily blocked by admin
    INACTIVE,       // disabled by owner (not deleted)
    DELETED         // soft-deleted / archived
}