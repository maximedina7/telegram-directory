package com.telegram.directory.query.repository;

import com.telegram.directory.query.model.Category;
import com.telegram.directory.query.model.Professional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de integración para ProfessionalRepository (Query)")
class ProfessionalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfessionalRepository professionalRepository;

    private Category testCategory;
    private Professional professional1;
    private Professional professional2;
    private Professional professional3;

    @BeforeEach
    void setUp() {
        // Limpiar base de datos antes de cada test
        professionalRepository.deleteAll();

        // Crear categoría de prueba
        testCategory = new Category("Plomería", "Trabajos de agua");
        entityManager.persist(testCategory);
        entityManager.flush();

        // Crear profesionales de prueba
        professional1 = new Professional(
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
        entityManager.persist(professional1);

        professional2 = new Professional(
                "electricista",
                "María González",
                "Buenos Aires",
                "+54 11 5555678",
                "maria@mail.com",
                12,
                "Instalaciones industriales",
                true,
                4.8,
                testCategory
        );
        entityManager.persist(professional2);

        professional3 = new Professional(
                "carpintero",
                "Pedro Martínez",
                "Rosario",
                "+54 341 5559999",
                "pedro@mail.com",
                5,
                "Muebles a medida",
                false,
                3.5,
                null
        );
        entityManager.persist(professional3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Debería buscar profesionales por oficio (case insensitive)")
    void testFindByTradeIgnoreCase() {
        // Act
        List<Professional> result = professionalRepository.findByTradeIgnoreCase("electricista");

        // Assert
        assertEquals(2, result.size());
        result.forEach(p -> assertEquals("electricista", p.getTrade().toLowerCase()));
    }

    @Test
    @DisplayName("Debería buscar profesionales por ciudad (case insensitive)")
    void testFindByCityIgnoreCase() {
        // Act
        List<Professional> result = professionalRepository.findByCityIgnoreCase("rosario");

        // Assert
        assertEquals(2, result.size());
        result.forEach(p -> assertEquals("Rosario", p.getCity()));
    }

    @Test
    @DisplayName("Debería buscar profesionales por oficio y ciudad")
    void testFindByTradeAndCity() {
        // Act
        List<Professional> result = professionalRepository.findByTradeAndCity("electricista", "Rosario");

        // Assert
        assertEquals(1, result.size());
        assertEquals("electricista", result.get(0).getTrade());
        assertEquals("Rosario", result.get(0).getCity());
        assertEquals("Juan Pérez", result.get(0).getName());
    }

    @Test
    @DisplayName("Debería buscar profesionales por categoría")
    void testFindByCategoryName() {
        // Act
        List<Professional> result = professionalRepository.findByCategoryName("Plomería");

        // Assert
        assertEquals(2, result.size());
        result.forEach(p -> {
            assertNotNull(p.getCategory());
            assertEquals("Plomería", p.getCategory().getName());
        });
    }

    @Test
    @DisplayName("Debería buscar profesionales verificados")
    void testFindByVerifiedTrue() {
        // Act
        List<Professional> result = professionalRepository.findByVerifiedTrue();

        // Assert
        assertEquals(2, result.size());
        result.forEach(p -> assertTrue(p.isVerified()));
    }

    @Test
    @DisplayName("Debería buscar profesionales con rating mínimo")
    void testFindByRatingGreaterThanEqual() {
        // Act
        List<Professional> result = professionalRepository.findByRatingGreaterThanEqual(4.5);

        // Assert
        assertEquals(2, result.size());
        result.forEach(p -> assertTrue(p.getRating() >= 4.5));
    }

    @Test
    @DisplayName("Debería retornar lista vacía si no hay coincidencias")
    void testFindByTradeIgnoreCase_NoResults() {
        // Act
        List<Professional> result = professionalRepository.findByTradeIgnoreCase("fotógrafo");

        // Assert
        assertTrue(result.isEmpty());
    }
}

