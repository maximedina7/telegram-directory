package com.telegram.directory.management.service;

import com.telegram.directory.management.model.Category;
import com.telegram.directory.management.model.Professional;
import com.telegram.directory.management.repository.CategoryRepository;
import com.telegram.directory.management.repository.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para ProfessionalService (Management)")
class ProfessionalServiceTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private CategoryRepository categoryRepository;

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
    @DisplayName("Debería agregar un profesional exitosamente")
    void testAddProfessional_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(professionalRepository.save(any(Professional.class))).thenReturn(testProfessional);

        // Act
        Professional result = professionalService.add(
                "electricista", "Juan Pérez", "Rosario",
                "+54 341 5551234", "juan@mail.com",
                8, "Especialista", true, 4.5, 1L
        );

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTrade());
        assertEquals("electricista", result.getTrade());
        assertEquals("Juan Pérez", result.getName());
        assertEquals(4.5, result.getRating());
        verify(categoryRepository).findById(1L);
        verify(professionalRepository).save(any(Professional.class));
    }

    @Test
    @DisplayName("Debería agregar un profesional sin categoría")
    void testAddProfessional_WithoutCategory() {
        // Arrange
        Professional profWithoutCategory = new Professional(
                "carpintero", "Pedro", "Buenos Aires",
                "+54 11 5551234", "pedro@mail.com",
                5, "Descripción", false, 4.0, null
        );
        profWithoutCategory.setId(2L);
        when(professionalRepository.save(any(Professional.class))).thenReturn(profWithoutCategory);

        // Act
        Professional result = professionalService.add(
                "carpintero", "Pedro", "Buenos Aires",
                "+54 11 5551234", "pedro@mail.com",
                5, "Descripción", false, 4.0, null
        );

        // Assert
        assertNotNull(result);
        assertNull(result.getCategory());
        verify(categoryRepository, never()).findById(any());
        verify(professionalRepository).save(any(Professional.class));
    }

    @Test
    @DisplayName("Debería normalizar rating a 0 si es negativo")
    void testAddProfessional_NormalizeRating_Negative() {
        // Arrange
        when(professionalRepository.save(any(Professional.class))).thenAnswer(invocation -> {
            Professional p = invocation.getArgument(0);
            assertEquals(0.0, p.getRating());
            return p;
        });

        // Act
        professionalService.add(
                "oficio", "Nombre", "Ciudad",
                "123", "email@mail.com",
                0, "", false, -5.0, null
        );

        // Assert
        verify(professionalRepository).save(any(Professional.class));
    }

    @Test
    @DisplayName("Debería normalizar rating a 5 si es mayor que 5")
    void testAddProfessional_NormalizeRating_TooHigh() {
        // Arrange
        when(professionalRepository.save(any(Professional.class))).thenAnswer(invocation -> {
            Professional p = invocation.getArgument(0);
            assertEquals(5.0, p.getRating());
            return p;
        });

        // Act
        professionalService.add(
                "oficio", "Nombre", "Ciudad",
                "123", "email@mail.com",
                0, "", false, 10.0, null
        );

        // Assert
        verify(professionalRepository).save(any(Professional.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si la categoría no existe")
    void testAddProfessional_CategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            professionalService.add(
                    "oficio", "Nombre", "Ciudad",
                    "123", "email@mail.com",
                    0, "", false, 4.0, 999L
            );
        });

        assertEquals("Categoría no encontrada con ID 999", exception.getMessage());
        verify(professionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería actualizar un profesional existente")
    void testUpdateProfessional_Success() {
        // Arrange
        Long id = 1L;
        when(professionalRepository.findById(id)).thenReturn(Optional.of(testProfessional));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(professionalRepository.save(any(Professional.class))).thenReturn(testProfessional);

        // Act
        Optional<Professional> result = professionalService.update(
                id, "plomero", "Juan Pérez Actualizado", "Córdoba",
                "+54 351 5555678", "juan.nuevo@mail.com",
                10, "Nueva descripción", false, 4.8, 1L
        );

        // Assert
        assertTrue(result.isPresent());
        Professional updated = result.get();
        assertNotNull(updated);
        assertEquals("plomero", updated.getTrade());
        assertEquals("Juan Pérez Actualizado", updated.getName());
        assertEquals(4.8, updated.getRating());
        verify(professionalRepository).findById(id);
        verify(professionalRepository).save(any(Professional.class));
    }

    @Test
    @DisplayName("Debería retornar Optional vacío al actualizar profesional inexistente")
    void testUpdateProfessional_NotFound() {
        // Arrange
        Long id = 999L;
        when(professionalRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Professional> result = professionalService.update(
                id, "oficio", "Nombre", "Ciudad",
                "123", "email@mail.com",
                0, "", false, 4.0, null
        );

        // Assert
        assertFalse(result.isPresent());
        verify(professionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar un profesional existente")
    void testDeleteProfessional_Success() {
        // Arrange
        Long id = 1L;
        when(professionalRepository.existsById(id)).thenReturn(true);

        // Act
        boolean result = professionalService.delete(id);

        // Assert
        assertTrue(result);
        verify(professionalRepository).existsById(id);
        verify(professionalRepository).deleteById(id);
    }

    @Test
    @DisplayName("Debería retornar false al eliminar profesional inexistente")
    void testDeleteProfessional_NotFound() {
        // Arrange
        Long id = 999L;
        when(professionalRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = professionalService.delete(id);

        // Assert
        assertFalse(result);
        verify(professionalRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debería listar todos los profesionales")
    void testListAll() {
        // Arrange
        Professional prof2 = new Professional(
                "carpintero", "Pedro", "Buenos Aires",
                "+54 11 5551234", "pedro@mail.com",
                5, "", false, 4.0, null
        );
        List<Professional> professionals = Arrays.asList(testProfessional, prof2);
        when(professionalRepository.findAll()).thenReturn(professionals);

        // Act
        List<Professional> result = professionalService.listAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(professionalRepository).findAll();
    }
}

