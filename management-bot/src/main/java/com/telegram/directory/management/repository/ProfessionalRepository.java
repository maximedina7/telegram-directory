package com.telegram.directory.management.repository;

import com.telegram.directory.management.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    boolean existsByCategory_Id(Long categoryId);
}

