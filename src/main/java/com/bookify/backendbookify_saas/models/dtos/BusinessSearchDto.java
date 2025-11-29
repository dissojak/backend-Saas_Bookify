package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessSearchDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Double rating; // average rating or null
    private String imageUrl; // first image or null
    private String description;
}

