package com.telegram.directory.management.service;

import com.telegram.directory.management.model.Professional;
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
    
    @Autowired
    public ProfessionalService(ProfessionalRepository repository) {
        this.repository = repository;
    }
    
    public Professional add(String trade, String name, String city) {
        Professional professional = new Professional(trade, name, city);
        return repository.save(professional);
    }
    
    public Optional<Professional> update(long id, String trade, String name, String city) {
        Optional<Professional> optional = repository.findById(id);
        if (optional.isPresent()) {
            Professional professional = optional.get();
            professional.setTrade(trade);
            professional.setName(name);
            professional.setCity(city);
            return Optional.of(repository.save(professional));
        }
        return Optional.empty();
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

