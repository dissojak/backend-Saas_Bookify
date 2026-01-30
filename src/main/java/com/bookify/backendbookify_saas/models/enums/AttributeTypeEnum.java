package com.bookify.backendbookify_saas.models.enums;

/**
 * Enum representing attribute value types for resource attributes and templates
 */
public enum AttributeTypeEnum {
    TEXT("Text"),
    NUMBER("Number"),
    BOOLEAN("Boolean");

    private final String displayName;

    AttributeTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
