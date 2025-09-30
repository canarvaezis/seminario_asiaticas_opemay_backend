package co.edu.uniajc.estudiante.opemay.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import co.edu.uniajc.estudiante.opemay.IRespository.CategoryRepository;
import co.edu.uniajc.estudiante.opemay.config.TestFirebaseConfig;
import co.edu.uniajc.estudiante.opemay.model.Category;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestFirebaseConfig.class)
@SuppressWarnings("deprecation") // Suppress MockBean deprecation warnings
class CategoryServiceTest {

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id("cat-123")
                .name("Test Category")
                .description("Test Description")
                .imageUrl("http://example.com/image.jpg")
                .sortOrder(1)
                .active(true)
                .build();
    }

    @Test
    void testCreateCategory_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryByName("New Category")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn("success");

        // Act
        Category result = categoryService.createCategory("New Category", "Description", "image.jpg", 1);

        // Assert
        assertNotNull(result);
        assertEquals("New Category", result.getName());
        assertEquals("Description", result.getDescription());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateCategory_EmptyName() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory("", "Description", null, null));
        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());
    }

    @Test
    void testCreateCategory_NullName() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory(null, "Description", null, null));
        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());
    }

    @Test
    void testCreateCategory_DuplicateName() throws ExecutionException, InterruptedException {
        // Arrange
        Category existingCategory = Category.builder()
                .id("existing-id")
                .name("Existing Category")
                .build();

        when(categoryRepository.getCategoryByName("Existing Category")).thenReturn(existingCategory);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory("Existing Category", "Description", null, null));
        assertTrue(exception.getMessage().contains("Ya existe una categoría con el nombre"));
    }

    @Test
    void testGetCategoryById_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("cat-123")).thenReturn(testCategory);

        // Act
        Category result = categoryService.getCategoryById("cat-123");

        // Assert
        assertNotNull(result);
        assertEquals(testCategory, result);
    }

    @Test
    void testGetCategoryById_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("nonexistent-id")).thenReturn(null);

        // Act
        Category result = categoryService.getCategoryById("nonexistent-id");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetCategoryById_InvalidId() {
        // Test con ID nulo
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.getCategoryById(null));
        assertEquals("El ID de la categoría no puede estar vacío", exception.getMessage());

        // Test con ID vacío
        exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.getCategoryById(""));
        assertEquals("El ID de la categoría no puede estar vacío", exception.getMessage());

        // Test con ID solo espacios
        exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.getCategoryById("   "));
        assertEquals("El ID de la categoría no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetCategoryByName_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryByName("Test Category")).thenReturn(testCategory);

        // Act
        Category result = categoryService.getCategoryByName("Test Category");

        // Assert
        assertNotNull(result);
        assertEquals(testCategory, result);
    }

    @Test
    void testGetCategoryByName_InvalidName() {
        // Test con nombre nulo
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.getCategoryByName(null));
        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());

        // Test con nombre vacío
        exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.getCategoryByName(""));
        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetAllActiveCategories() throws ExecutionException, InterruptedException {
        // Arrange
        List<Category> expectedCategories = Arrays.asList(testCategory);
        when(categoryRepository.getAllActiveCategories()).thenReturn(expectedCategories);

        // Act
        List<Category> result = categoryService.getAllActiveCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCategory, result.get(0));
    }

    @Test
    void testGetAllCategories() throws ExecutionException, InterruptedException {
        // Arrange
        List<Category> expectedCategories = Arrays.asList(testCategory);
        when(categoryRepository.getAllCategories()).thenReturn(expectedCategories);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCategory, result.get(0));
    }

    @Test
    void testUpdateCategory_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("cat-123")).thenReturn(testCategory);
        when(categoryRepository.getCategoryByName("Updated Name")).thenReturn(null);
        when(categoryRepository.update(any(Category.class))).thenReturn("success");

        // Act
        Category result = categoryService.updateCategory("cat-123", "Updated Name", "New Description", "new-image.jpg", 2);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals("new-image.jpg", result.getImageUrl());
        assertEquals(2, result.getSortOrder());
        verify(categoryRepository).update(any(Category.class));
    }

    @Test
    void testUpdateCategory_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("nonexistent-id")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory("nonexistent-id", "New Name", null, null, null));
        assertEquals("Categoría no encontrada", exception.getMessage());
    }

    @Test
    void testUpdateCategory_DuplicateName() throws ExecutionException, InterruptedException {
        // Arrange
        Category categoryToUpdate = Category.builder()
                .id("category-1")
                .name("Original Name")
                .build();

        Category existingCategory = Category.builder()
                .id("category-2")
                .name("Duplicate Name")
                .build();

        when(categoryRepository.getCategoryById("category-1")).thenReturn(categoryToUpdate);
        when(categoryRepository.getCategoryByName("Duplicate Name")).thenReturn(existingCategory);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory("category-1", "Duplicate Name", null, null, null));
        assertTrue(exception.getMessage().contains("Ya existe una categoría con el nombre"));
    }

    @Test
    void testToggleCategoryStatus_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("cat-123")).thenReturn(testCategory);
        when(categoryRepository.update(any(Category.class))).thenReturn("success");

        // Act
        Category result = categoryService.toggleCategoryStatus("cat-123");

        // Assert
        assertNotNull(result);
        assertFalse(result.getActive()); // Should be toggled from true to false
        verify(categoryRepository).update(any(Category.class));
    }

    @Test
    void testToggleCategoryStatus_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("nonexistent-id")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.toggleCategoryStatus("nonexistent-id"));
        assertEquals("Categoría no encontrada", exception.getMessage());
    }

    @Test
    void testDeleteCategory_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("cat-123")).thenReturn(testCategory);
        when(categoryRepository.softDelete("cat-123")).thenReturn("success");

        // Act
        assertDoesNotThrow(() -> categoryService.deleteCategory("cat-123"));

        // Assert
        verify(categoryRepository).softDelete("cat-123");
    }

    @Test
    void testDeleteCategory_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("nonexistent-id")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.deleteCategory("nonexistent-id"));
        assertEquals("Categoría no encontrada", exception.getMessage());
    }

    @Test
    void testValidateCategory_Success() {
        // Arrange
        Category validCategory = Category.builder()
                .name("Valid Name")
                .description("Valid description")
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> categoryService.validateCategory(validCategory));
    }

    @Test
    void testValidateCategory_NullCategory() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.validateCategory(null));
        assertEquals("La categoría no puede ser null", exception.getMessage());
    }

    @Test
    void testValidateCategory_InvalidCategory() {
        // Arrange
        Category invalidCategory = Category.builder()
                .name("")
                .description("Valid description")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.validateCategory(invalidCategory));
        assertEquals("Los datos de la categoría no son válidos", exception.getMessage());
    }

    @Test
    void testCreateCategory_WithDefaultSortOrder() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryByName("New Category")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn("success");

        // Act
        Category result = categoryService.createCategory("New Category", "Description", null, null);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getSortOrder()); // Default sort order
    }

    @Test
    void testCreateCategory_TrimsName() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryByName("Trimmed Category")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn("success");

        // Act
        Category result = categoryService.createCategory("  Trimmed Category  ", "Description", null, null);

        // Assert
        assertNotNull(result);
        assertEquals("Trimmed Category", result.getName());
    }

    @Test
    void testUpdateCategory_NoNameChange() throws ExecutionException, InterruptedException {
        // Arrange
        when(categoryRepository.getCategoryById("cat-123")).thenReturn(testCategory);
        when(categoryRepository.update(any(Category.class))).thenReturn("success");

        // Act
        Category result = categoryService.updateCategory("cat-123", null, "New Description", null, null);

        // Assert
        assertNotNull(result);
        assertEquals("Test Category", result.getName()); // Original name preserved
        assertEquals("New Description", result.getDescription());
    }
}