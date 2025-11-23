package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessImageRepository extends JpaRepository<BusinessImage, Long> {

    List<BusinessImage> findByBusinessOrderByDisplayOrderAsc(Business business);

    List<BusinessImage> findByBusinessIdOrderByDisplayOrderAsc(Long businessId);

    boolean existsByBusinessIdAndImageUrl(Long businessId, String imageUrl);

    void deleteByBusinessId(Long businessId);
}
