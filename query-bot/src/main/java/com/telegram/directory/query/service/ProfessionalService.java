package com.telegram.directory.query.service;

import com.telegram.directory.query.model.Professional;
import com.telegram.directory.query.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProfessionalService {
    
    private final ProfessionalRepository repository;
    
    @Autowired
    public ProfessionalService(ProfessionalRepository repository) {
        this.repository = repository;
    }
    
    public List<Professional> findByTrade(String trade) {
        return repository.findByTradeIgnoreCase(trade);
    }
    
    public List<Professional> findByCity(String city) {
        return repository.findByCityIgnoreCase(city);
    }
    
    public List<Professional> findByTradeAndCity(String trade, String city) {
        return repository.findByTradeAndCity(trade, city);
    }

    public List<Professional> findByCategory(String category) {
        return repository.findByCategoryName(category);
    }

    public List<Professional> findVerified() {
        return repository.findByVerifiedTrue();
    }

    public List<Professional> findTopRated(double minRating) {
        return repository.findByRatingGreaterThanEqual(minRating);
    }
}

