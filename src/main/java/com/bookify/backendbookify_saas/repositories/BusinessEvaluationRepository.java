package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessEvaluationRepository extends JpaRepository<BusinessEvaluation, Long> {
    List<BusinessEvaluation> findByBusinessOrderByCreatedAtDesc(Business business);
}

