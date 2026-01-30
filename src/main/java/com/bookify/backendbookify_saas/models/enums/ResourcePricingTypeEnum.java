package com.bookify.backendbookify_saas.models.enums;

/**
 * Enum representing different pricing models for resources
 */
public enum ResourcePricingTypeEnum {
    PER_HOUR("Per Hour"),
    PER_HALF_DAY("Per Half Day"),
    PER_DAY("Per Day"),
    PER_WEEK("Per Week"),
    PER_MONTH("Per Month"),
    PER_PERSON("Per Person"),
    FLAT_RATE("Flat Rate");

    private final String displayName;

    ResourcePricingTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
