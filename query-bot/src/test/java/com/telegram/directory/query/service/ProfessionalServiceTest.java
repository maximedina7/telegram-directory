package com.telegram.directory.query.service;

import com.telegram.directory.query.model.Category;
import com.telegram.directory.query.model.Professional;
import com.telegram.directory.query.repository.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para ProfessionalService (Query)")
class ProfessionalServiceTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private ProfessionalService professionalService;

    private Professional testProfessional;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Plomería", "Descripción");
        testCategory.setId(1L);

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
        testProfessional.setId(1L);
    }

    @Test
    @DisplayName("Debería buscar profesionales por oficio")
    void testFindByTrade() {
        // Arrange
        List<Professional> professionals = Arrays.asList(testProfessional);
        when(professionalRepository.findByTradeIgnoreCase("electricista")).thenReturn(professionals);

        // Act
        List<Professional> result = professionalService.findByTrade("electricista");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("electricista", result.get(0).getTrade());
        verify(professionalRepository).findByTradeIgnoreCase("electricista");
    }

    @Test
    @DisplayName("Debería buscar profesionales por oficio ignorando mayúsculas")
    void testFindByTrade_CaseInsensitive() {
        // Arrange
        List<Professional> professionals = Arrays.asList(testProfessional);
        when(professionalRepository.findByTradeIgnoreCase("ELECTRICISTA")).thenReturn(professionals);

        // Act
        List<Professional> result = professionalService.findByTrade("ELECTRICISTA");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(professionalRepository).findByTradeIgnoreCase("ELECTRICISTA");
    }

    @Test
    @DisplayName("Debería retornar lista vacía si no hay profesionales con ese oficio")
    void testFindByTrade_EmptyResult() {
        // Arrange
        when(professionalRepository.findByTradeIgnoreCase(anyString())).thenReturn(Collections.emptyList());

        // Act
        List<Professional> result = professionalService.findByTrade("carpintero");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(professionalRepository).findByTradeIgnoreCase("carpintero");
    }

    @Test
    @DisplayName("Debería buscar profesionales por ciudad")
    void testFindByCity() {
        // Arrange
        List<Professional> professionals = Arrays.asList(testProfessional);
        when(professionalRepository.findByCityIgnoreCase("Rosario")).thenReturn(professionals);

        // Act
        List<Professional> result = professionalService.findByCity("Rosario");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Rosario", result.get(0).getCity());
        verify(professionalRepository).findByCityIgnoreCase("Rosario");
    }

    @Test
    @DisplayName("Debería buscar profesionales por oficio y ciudad")
    void testFindByTradeAndCity() {
        // Arrange
        List<Professional> professionals = Arrays.asList(testProfessional);
        when(professionalRepository.findByTradeAndCity("electricista", "Rosario")).thenReturn(professionals);

        // Act
        List<Professional> result = professionalService.findByTradeAndCity("electricista", "Rosario");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(professionalRepository).findByTradeAndCity("electricista", "Rosario");
    }

    @Test
    @DisplayName("Debería buscar profesionales por categoría")
    void testFindByCategory() {
        // Arrange
        List<Professional> professionals = Arrays.asList(testProfessional);
        when(professionalRepository.findByCategoryName("Plomería")).thenReturn(professionals);

        // Act
        List<Professional> result = professionalService.findByCategory("Plomería");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(professionalRepository).findByCategoryName("Plomería");
    }

    @Test
    @DisplayName("Debería buscar profesionales verificados")
    void testFindVerified() {
        // Arrange
        Professional verifiedProf = new Professional(
                "plomero", "Carlos", "Buenos Aires",
                "+54 11 5551234", "carlos@mail.com",
                10, "", true, 4.8, testCategory
        );
        List<Professional> professionals = Arrays.asList(verifiedProf);
        when(professionalRepository.findByVerifiedTrue()).thenReturn(professionals);

        // Act
        List<Professional> result = professionalService.findVerified();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isVerified());
        verify(professionalRepository).findByVerifiedTrue();
    }

    @Test
    @DisplayName("Debería buscar profesionales con rating mínimo")
    void testFindTopRated() {
        // Arrange
        Professional topProf = new Professional(
                "electricista", "María", "Córdoba",
                "+54 351 5551234", "maria@mail.com",
                15, "", true, 4.9, testCategory
        );
        List<Professional> professionals = Arrays.asList(topProf);
        when(professionalRepository.findByRatingGreaterThanEqual(4.5)).thenReturn(professionals);

        // Act
        List<Professional> result = professionalService.findTopRated(4.5);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getRating() >= 4.5);
        verify(professionalRepository).findByRatingGreaterThanEqual(4.5);
    }

    @Test
    @DisplayName("Debería retornar lista vacía si no hay profesionales con rating suficiente")
    void testFindTopRated_EmptyResult() {
        // Arrange
        when(professionalRepository.findByRatingGreaterThanEqual(anyDouble())).thenReturn(Collections.emptyList());

        // Act
        List<Professional> result = professionalService.findTopRated(5.0);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(professionalRepository).findByRatingGreaterThanEqual(5.0);
    }
}

