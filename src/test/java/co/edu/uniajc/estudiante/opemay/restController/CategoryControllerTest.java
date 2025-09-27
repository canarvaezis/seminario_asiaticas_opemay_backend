package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uniajc.estudiante.opemay.Service.CategoryService;
import co.edu.uniajc.estudiante.opemay.config.TestFirebaseConfig;
import co.edu.uniajc.estudiante.opemay.config.TestSecurityConfig;
import co.edu.uniajc.estudiante.opemay.dto.CategoryCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CategoryUpdateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CreateCategoryRequest;
import co.edu.uniajc.estudiante.opemay.dto.UpdateCategoryRequest;
import co.edu.uniajc.estudiante.opemay.model.Category;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
@Import({TestFirebaseConfig.class, TestSecurityConfig.class})
@SuppressWarnings("deprecation") // Suppress MockBean deprecation warnings
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category testCategory;
    private List<Category> testCategories;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id("cat-123")
                .name("Test Category")
                .description("Test category description")
                .slug("test-category")
                .imageUrl("https://example.com/image.jpg")
                .sortOrder(1)
                .active(true)
                .build();

        Category category2 = Category.builder()
                .id("cat-456")
                .name("Another Category")
                .description("Another category description")
                .slug("another-category")
                .sortOrder(2)
                .active(true)
                .build();

        testCategories = Arrays.asList(testCategory, category2);

        createRequest = new CreateCategoryRequest();
        createRequest.setName("New Category");
        createRequest.setDescription("New category description");
        createRequest.setImageUrl("https://example.com/new-image.jpg");
        createRequest.setSortOrder(3);

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setDescription("Updated category description");
        updateRequest.setImageUrl("https://example.com/updated-image.jpg");
        updateRequest.setSortOrder(5);
    }

    @Test
    @WithMockUser
    void testGetAllActiveCategories_Success() throws Exception {
        // Arrange
        when(categoryService.getAllActiveCategories()).thenReturn(testCategories);

        // Act & Assert
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("cat-123"))
                .andExpect(jsonPath("$[0].name").value("Test Category"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value("cat-456"))
                .andExpect(jsonPath("$[1].name").value("Another Category"));

        verify(categoryService).getAllActiveCategories();
    }

    @Test
    @WithMockUser
    void testGetAllActiveCategories_ExecutionException() throws Exception {
        // Arrange
        when(categoryService.getAllActiveCategories()).thenThrow(new ExecutionException("Database error", new RuntimeException()));

        // Act & Assert
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(categoryService).getAllActiveCategories();
    }

    @Test
    @WithMockUser
    void testGetAllActiveCategories_InterruptedException() throws Exception {
        // Arrange
        when(categoryService.getAllActiveCategories()).thenThrow(new InterruptedException("Thread interrupted"));

        // Act & Assert
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(categoryService).getAllActiveCategories();
    }

    @Test
    @WithMockUser
    void testGetAllCategories_Success() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(testCategories);

        // Act & Assert
        mockMvc.perform(get("/api/categories/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(categoryService).getAllCategories();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_Success() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CategoryCreateDTO.class))).thenReturn(testCategory);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("cat-123"))
                .andExpect(jsonPath("$.name").value("Test Category"))
                .andExpect(jsonPath("$.description").value("Test category description"));

        verify(categoryService).createCategory(any(CategoryCreateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_ExecutionException() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CategoryCreateDTO.class)))
                .thenThrow(new ExecutionException("Database error", new RuntimeException()));

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isInternalServerError());

        verify(categoryService).createCategory(any(CategoryCreateDTO.class));
    }

    @Test
    @WithMockUser
    void testGetCategoryById_Success() throws Exception {
        // Arrange
        when(categoryService.getCategoryById("cat-123")).thenReturn(testCategory);

        // Act & Assert
        mockMvc.perform(get("/api/categories/cat-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cat-123"))
                .andExpect(jsonPath("$.name").value("Test Category"));

        verify(categoryService).getCategoryById("cat-123");
    }

    @Test
    @WithMockUser
    void testGetCategoryById_NotFound() throws Exception {
        // Arrange
        when(categoryService.getCategoryById("nonexistent")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/categories/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService).getCategoryById("nonexistent");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory_Success() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq("cat-123"), any(CategoryUpdateDTO.class)))
                .thenReturn(testCategory);

        // Act & Assert
        mockMvc.perform(put("/api/categories/cat-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cat-123"))
                .andExpect(jsonPath("$.name").value("Test Category"));

        verify(categoryService).updateCategory(eq("cat-123"), any(CategoryUpdateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory_NotFound() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq("nonexistent"), any(CategoryUpdateDTO.class)))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/api/categories/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(categoryService).updateCategory(eq("nonexistent"), any(CategoryUpdateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_Success() throws Exception {
        // Arrange - deleteCategory es void, no necesita mock de retorno

        // Act & Assert
        mockMvc.perform(delete("/api/categories/cat-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory("cat-123");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_NotFound() throws Exception {
        // Arrange - deleteCategory es void, no necesita mock de retorno

        // Act & Assert
        mockMvc.perform(delete("/api/categories/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService).deleteCategory("nonexistent");
    }

    @Test
    @WithMockUser
    void testGetCategoryBySlug_Success() throws Exception {
        // Arrange
        when(categoryService.getCategoryBySlug("test-category")).thenReturn(testCategory);

        // Act & Assert
        mockMvc.perform(get("/api/categories/slug/test-category")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cat-123"))
                .andExpect(jsonPath("$.slug").value("test-category"));

        verify(categoryService).getCategoryBySlug("test-category");
    }

    @Test
    @WithMockUser
    void testGetCategoryBySlug_NotFound() throws Exception {
        // Arrange
        when(categoryService.getCategoryBySlug("nonexistent-slug")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/categories/slug/nonexistent-slug")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService).getCategoryBySlug("nonexistent-slug");
    }

    @Test
    void testControllerExists() {
        CategoryController controller = new CategoryController();
        assertNotNull(controller);
    }

    @Test
    @WithMockUser
    void testGetAllActiveCategories_EmptyList() throws Exception {
        // Arrange
        when(categoryService.getAllActiveCategories()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateCategory_Forbidden() throws Exception {
        // Act & Assert - Usuario sin rol ADMIN no puede crear categorías
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateCategory_Forbidden() throws Exception {
        // Act & Assert - Usuario sin rol ADMIN no puede actualizar categorías
        mockMvc.perform(put("/api/categories/cat-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteCategory_Forbidden() throws Exception {
        // Act & Assert - Usuario sin rol ADMIN no puede eliminar categorías
        mockMvc.perform(delete("/api/categories/cat-123"))
                .andExpect(status().isForbidden());
    }
}