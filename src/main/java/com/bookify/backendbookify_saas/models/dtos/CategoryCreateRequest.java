package com.bookify.backendbookify_saas.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private String icon;
}
