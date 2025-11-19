package com.telegram.directory.management.repository;

import com.telegram.directory.management.model.Category;
import com.telegram.directory.management.model.Professional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("null")

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de integración para ProfessionalRepository")
class ProfessionalRepositoryTest {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;
    private Professional testProfessional;

    @BeforeEach
    void setUp() {
        // Limpiar base de datos antes de cada test
        professionalRepository.deleteAll();
        categoryRepository.deleteAll();

        // Crear categoría de prueba
        testCategory = new Category("Plomería", "Trabajos de agua");
        testCategory = categoryRepository.save(testCategory);

        // Crear profesional de prueba
        testProfessional = new Professional(
                "electricista",
                "Juan Pérez",
                "Rosario",
                "+54 341 5551234",
                "juan@mail.com",
                8,
                "Especialista en instalaciones",
                true,
                4.5,
                testCategory
        );
        testProfessional = professionalRepository.save(testProfessional);
    }

    @Test
    @DisplayName("Debería guardar y recuperar un profesional")
    void testSaveAndFind() {
        // Arrange
        Long professionalId = Objects.requireNonNull(testProfessional.getId(), "ID del profesional no puede ser null");
        
        // Act
        Professional found = professionalRepository.findById(professionalId).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals("electricista", found.getTrade());
        assertEquals("Juan Pérez", found.getName());
        assertEquals("Rosario", found.getCity());
        assertEquals(4.5, found.getRating());
        assertTrue(found.isVerified());
    }

    @Test
    @DisplayName("Debería verificar si existe profesional asociado a una categoría")
    void testExistsByCategoryId() {
        // Arrange
        Long categoryId = Objects.requireNonNull(testCategory.getId(), "ID de categoría no puede ser null");
        
        // Act
        boolean exists = professionalRepository.existsByCategory_Id(categoryId);

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debería retornar false si no hay profesionales en la categoría")
    void testExistsByCategoryId_NoProfessionals() {
        // Arrange
        Category newCategory = new Category("Carpintería", "Trabajos en madera");
        newCategory = categoryRepository.save(newCategory);
        Long categoryId = Objects.requireNonNull(newCategory.getId(), "ID de categoría no puede ser null");

        // Act
        boolean exists = professionalRepository.existsByCategory_Id(categoryId);

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Debería eliminar un profesional correctamente")
    void testDelete() {
        // Arrange
        Long professionalId = Objects.requireNonNull(testProfessional.getId(), "ID del profesional no puede ser null");
        
        // Act
        professionalRepository.deleteById(professionalId);

        // Assert
        assertFalse(professionalRepository.existsById(professionalId));
    }

    @Test
    @DisplayName("Debería listar todos los profesionales")
    void testFindAll() {
        // Arrange
        Professional prof2 = new Professional(
                "carpintero", "Pedro", "Buenos Aires",
                "+54 11 5551234", "pedro@mail.com",
                5, "", false, 4.0, null
        );
        professionalRepository.save(prof2);

        // Act
        List<Professional> all = professionalRepository.findAll();

        // Assert
        assertEquals(2, all.size());
    }
}

