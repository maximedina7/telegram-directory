package com.telegram.directory.query.repository;

import com.telegram.directory.query.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    List<Professional> findByTradeIgnoreCase(String trade);

    List<Professional> findByCityIgnoreCase(String city);

    @Query("SELECT p FROM Professional p WHERE LOWER(p.trade) = LOWER(:trade) AND LOWER(p.city) = LOWER(:city)")
    List<Professional> findByTradeAndCity(@Param("trade") String trade, @Param("city") String city);

    @Query("SELECT p FROM Professional p WHERE LOWER(p.category.name) = LOWER(:name)")
    List<Professional> findByCategoryName(@Param("name") String name);

    List<Professional> findByVerifiedTrue();

    List<Professional> findByRatingGreaterThanEqual(double rating);
}

