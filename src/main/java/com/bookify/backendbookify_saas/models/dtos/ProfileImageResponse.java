package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned after uploading a profile image.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageResponse {
    private String url;
    private String publicId;
}
