package com.telegram.directory.management.service;

import com.telegram.directory.management.model.Category;
import com.telegram.directory.management.model.Professional;
import com.telegram.directory.management.repository.CategoryRepository;
import com.telegram.directory.management.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfessionalService {
    
    private final ProfessionalRepository repository;
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public ProfessionalService(ProfessionalRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }
    
    public Professional add(String trade,
                            String name,
                            String city,
                            String phone,
                            String email,
                            int experienceYears,
                            String description,
                            boolean verified,
                            double rating,
                            Long categoryId) {
        Category category = resolveCategory(categoryId);
        double normalizedRating = normalizeRating(rating);
        Professional professional = new Professional(trade, name, city, phone, email, experienceYears, description, verified, normalizedRating, category);
        return repository.save(professional);
    }
    
    public Optional<Professional> update(long id,
                                         String trade,
                                         String name,
                                         String city,
                                         String phone,
                                         String email,
                                         int experienceYears,
                                         String description,
                                         boolean verified,
                                         double rating,
                                         Long categoryId) {
        Optional<Professional> optional = repository.findById(id);
        if (optional.isPresent()) {
            Professional professional = optional.get();
            professional.setTrade(trade);
            professional.setName(name);
            professional.setCity(city);
            professional.setPhone(phone);
            professional.setEmail(email);
            professional.setExperienceYears(experienceYears);
            professional.setDescription(description);
            professional.setVerified(verified);
            professional.setRating(normalizeRating(rating));
            Category category = resolveCategory(categoryId);
            professional.setCategory(category);
            return Optional.of(repository.save(professional));
        }
        return Optional.empty();
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID " + categoryId));
    }

    private double normalizeRating(double rating) {
        if (rating < 0) {
            return 0;
        }
        if (rating > 5) {
            return 5;
        }
        return rating;
    }
    
    public boolean delete(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Transactional(readOnly = true)
    public List<Professional> listAll() {
        return repository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Professional> findById(long id) {
        return repository.findById(id);
    }
}

