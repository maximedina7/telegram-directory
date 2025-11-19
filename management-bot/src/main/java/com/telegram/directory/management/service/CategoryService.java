package com.telegram.directory.management.service;

import com.telegram.directory.management.model.Category;
import com.telegram.directory.management.repository.CategoryRepository;
import com.telegram.directory.management.repository.ProfessionalRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository repository;
    private final ProfessionalRepository professionalRepository;

    public CategoryService(CategoryRepository repository, ProfessionalRepository professionalRepository) {
        this.repository = repository;
        this.professionalRepository = professionalRepository;
    }

    public Category create(String name, String description) {
        repository.findByNameIgnoreCase(name).ifPresent(existing -> {
            throw new IllegalArgumentException("La categoría '" + name + "' ya existe");
        });
        Category category = new Category(name, description);
        return repository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> listAll() {
        return repository.findAll();
    }

    public boolean delete(@NonNull Long id) {
        Objects.requireNonNull(id, "El ID no puede ser null");
        if (!repository.existsById(id)) {
            return false;
        }
        if (professionalRepository.existsByCategory_Id(id)) {
            throw new IllegalStateException("No se puede eliminar la categoría: hay profesionales asociados");
        }
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Category> findById(@NonNull Long id) {
        Objects.requireNonNull(id, "El ID no puede ser null");
        return repository.findById(id);
    }
}


