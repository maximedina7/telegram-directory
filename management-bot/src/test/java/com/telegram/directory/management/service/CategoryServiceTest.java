package com.telegram.directory.management.service;

import com.telegram.directory.management.model.Category;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para CategoryService")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Plomería", "Trabajos de agua y fontanería");
        testCategory.setId(1L);
    }

    @Test
    @DisplayName("Debería crear una categoría exitosamente")
    void testCreateCategory_Success() {
        // Arrange
        when(categoryRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category result = categoryService.create("Plomería", "Trabajos de agua y fontanería");

        // Assert
        assertNotNull(result);
        assertEquals("Plomería", result.getName());
        assertEquals("Trabajos de agua y fontanería", result.getDescription());
        verify(categoryRepository).findByNameIgnoreCase("Plomería");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si la categoría ya existe")
    void testCreateCategory_DuplicateName() {
        // Arrange
        when(categoryRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(testCategory));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.create("Plomería", "Descripción");
        });

        assertEquals("La categoría 'Plomería' ya existe", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Debería listar todas las categorías")
    void testListAll() {
        // Arrange
        Category category1 = new Category("Plomería", "Descripción 1");
        Category category2 = new Category("Electricidad", "Descripción 2");
        List<Category> categories = Arrays.asList(category1, category2);
        
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.listAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Debería eliminar una categoría si no tiene profesionales asociados")
    void testDeleteCategory_Success() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(professionalRepository.existsByCategory_Id(categoryId)).thenReturn(false);

        // Act
        boolean result = categoryService.delete(categoryId);

        // Assert
        assertTrue(result);
        verify(categoryRepository).existsById(categoryId);
        verify(professionalRepository).existsByCategory_Id(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar categoría con profesionales asociados")
    void testDeleteCategory_WithProfessionals() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(professionalRepository.existsByCategory_Id(categoryId)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            categoryService.delete(categoryId);
        });

        assertEquals("No se puede eliminar la categoría: hay profesionales asociados", exception.getMessage());
        verify(categoryRepository, never()).deleteById(categoryId);
    }

    @Test
    @DisplayName("Debería retornar false al eliminar categoría inexistente")
    void testDeleteCategory_NotFound() {
        // Arrange
        Long categoryId = 999L;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // Act
        boolean result = categoryService.delete(categoryId);

        // Assert
        assertFalse(result);
        verify(categoryRepository).existsById(categoryId);
        verify(professionalRepository, never()).existsByCategory_Id(any());
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debería encontrar categoría por ID")
    void testFindById() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // Act
        Optional<Category> result = categoryService.findById(categoryId);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get());
        assertEquals(testCategory, result.get());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Debería retornar Optional vacío si la categoría no existe")
    void testFindById_NotFound() {
        // Arrange
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.findById(categoryId);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository).findById(categoryId);
    }
}

