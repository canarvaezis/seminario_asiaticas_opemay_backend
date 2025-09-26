package co.edu.uniajc.estudiante.opemay.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.cloud.Timestamp;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id("cat-123")
                .name("Frutas")
                .description("Frutas frescas y deliciosas")
                .slug("frutas")
                .enabled(true)
                .createdAt(Timestamp.now())
                .updatedAt(Timestamp.now())
                .build();
    }

    @Test
    void testBuilder() {
        assertNotNull(category);
        assertEquals("cat-123", category.getId());
        assertEquals("Frutas", category.getName());
        assertEquals("Frutas frescas y deliciosas", category.getDescription());
        assertEquals("frutas", category.getSlug());
        assertTrue(category.getEnabled());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        Category emptyCategory = new Category();
        assertNotNull(emptyCategory);
        assertNull(emptyCategory.getId());
        assertNull(emptyCategory.getName());
        assertNull(emptyCategory.getDescription());
    }

    @Test
    void testSettersAndGetters() {
        Category testCategory = new Category();
        
        testCategory.setId("test-id");
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        testCategory.setSlug("test-category");
        testCategory.setEnabled(false);
        
        Timestamp now = Timestamp.now();
        testCategory.setCreatedAt(now);
        testCategory.setUpdatedAt(now);
        
        assertEquals("test-id", testCategory.getId());
        assertEquals("Test Category", testCategory.getName());
        assertEquals("Test Description", testCategory.getDescription());
        assertEquals("test-category", testCategory.getSlug());
        assertFalse(testCategory.getEnabled());
        assertEquals(now, testCategory.getCreatedAt());
        assertEquals(now, testCategory.getUpdatedAt());
    }

    @Test
    void testGenerateSlugFromName() {
        // Arrange
        Category category1 = Category.builder().name("Frutas").build();
        Category category2 = Category.builder().name("Frutas Frescas").build();
        Category category3 = Category.builder().name("Frutas & Verduras").build();
        Category category4 = Category.builder().name("Lácteos").build();
        Category category5 = Category.builder().name("Productos   Orgánicos").build();
        Category category6 = Category.builder().name("Another Category").build();
        
        // Act
        category1.generateSlugFromName();
        category2.generateSlugFromName();
        category3.generateSlugFromName();
        category4.generateSlugFromName();
        category5.generateSlugFromName();
        category6.generateSlugFromName();
        
        // Assert
        assertEquals("frutas", category1.getSlug());
        assertEquals("frutas-frescas", category2.getSlug());
        assertNotNull(category3.getSlug());
        assertFalse(category3.getSlug().contains("&"));
        assertNotNull(category4.getSlug());
        assertFalse(category4.getSlug().contains("á"));
        assertNotNull(category5.getSlug());
        assertFalse(category5.getSlug().contains("   "));
        assertEquals("another-category", category6.getSlug());
    }

    @Test
    void testGenerateSlugFromNameWithNullName() {
        Category categoryWithNullName = Category.builder().name(null).build();
        categoryWithNullName.generateSlugFromName();
        assertNull(categoryWithNullName.getSlug());
    }

    @Test
    void testGenerateSlugFromNameWithEmptyName() {
        Category categoryWithEmptyName = Category.builder().name("").build();
        categoryWithEmptyName.generateSlugFromName();
        assertEquals("", categoryWithEmptyName.getSlug());
    }

    @Test
    void testGenerateSlugFromNameWithBlankName() {
        Category categoryWithBlankName = Category.builder().name("   ").build();
        categoryWithBlankName.generateSlugFromName();
        assertEquals("", categoryWithBlankName.getSlug());
    }

    @Test
    void testEnabledDefaultValue() {
        Category defaultCategory = Category.builder()
                .name("Test")
                .build();
        
        // El valor por defecto debería ser null, no false
        assertNull(defaultCategory.getEnabled());
    }

    @Test
    void testTimestampFields() {
        Timestamp specificTime = Timestamp.of(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        
        Category timedCategory = Category.builder()
                .name("Timed Category")
                .createdAt(specificTime)
                .updatedAt(specificTime)
                .build();
        
        assertEquals(specificTime, timedCategory.getCreatedAt());
        assertEquals(specificTime, timedCategory.getUpdatedAt());
    }

    @Test
    void testToString() {
        String categoryString = category.toString();
        assertNotNull(categoryString);
        // Lombok @Data genera toString con todos los campos
        assertTrue(categoryString.contains("Frutas") || categoryString.contains("cat-123"));
    }

    @Test
    void testEquals() {
        Category sameCategory = Category.builder()
                .id("cat-123")
                .name("Frutas")
                .description("Frutas frescas y deliciosas")
                .slug("frutas")
                .enabled(true)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
        
        assertEquals(category, sameCategory);
    }

    @Test
    void testHashCode() {
        Category sameCategory = Category.builder()
                .id("cat-123")
                .name("Frutas")
                .description("Frutas frescas y deliciosas")
                .slug("frutas")
                .enabled(true)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
        
        assertEquals(category.hashCode(), sameCategory.hashCode());
    }

    @Test
    void testBuilderPattern() {
        Category builtCategory = Category.builder()
                .id("builder-test")
                .name("Builder Test")
                .description("Testing builder pattern")
                .slug("builder-test")
                .enabled(false)
                .build();
        
        assertNotNull(builtCategory);
        assertEquals("builder-test", builtCategory.getId());
        assertEquals("Builder Test", builtCategory.getName());
        assertEquals("Testing builder pattern", builtCategory.getDescription());
        assertEquals("builder-test", builtCategory.getSlug());
        assertFalse(builtCategory.getEnabled());
    }

    @Test
    void testSlugGeneration_SpecialCharacters() {
        Category specialCategory = Category.builder()
                .name("Café & Té")
                .build();
        
        specialCategory.generateSlugFromName();
        
        assertNotNull(specialCategory.getSlug());
        // El slug no debería contener caracteres especiales
        assertFalse(specialCategory.getSlug().contains("&"));
        assertFalse(specialCategory.getSlug().contains("é"));
    }

    @Test
    void testSlugGeneration_MultipleSpaces() {
        Category spacedCategory = Category.builder()
                .name("Multiple    Spaces    Here")
                .build();
        
        spacedCategory.generateSlugFromName();
        
        assertNotNull(spacedCategory.getSlug());
        // No debería tener espacios múltiples en el slug
        assertFalse(spacedCategory.getSlug().contains("  "));
    }

    @Test
    void testSlugGeneration_Numbers() {
        Category numberedCategory = Category.builder()
                .name("Category 123")
                .build();
        
        numberedCategory.generateSlugFromName();
        
        assertNotNull(numberedCategory.getSlug());
        assertEquals("category-123", numberedCategory.getSlug());
    }

    @Test
    void testCategoryWithAllFieldsNull() {
        Category nullCategory = new Category();
        
        assertNull(nullCategory.getId());
        assertNull(nullCategory.getName());
        assertNull(nullCategory.getDescription());
        assertNull(nullCategory.getSlug());
        assertNull(nullCategory.getEnabled());
        assertNull(nullCategory.getCreatedAt());
        assertNull(nullCategory.getUpdatedAt());
    }
}