package co.edu.uniajc.estudiante.opemay.model;

import static org.junit.jupiter.api.Assertions.*;

import com.google.cloud.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id("cat-123")
                .name("Frutas")
                .description("Frutas frescas y naturales")
                .imageUrl("http://example.com/frutas.jpg")
                .sortOrder(1)
                .active(true)
                .build();
    }

    @Test
    void testCategoryBuilder() {
        assertNotNull(category);
        assertEquals("cat-123", category.getId());
        assertEquals("Frutas", category.getName());
        assertEquals("Frutas frescas y naturales", category.getDescription());
        assertEquals("http://example.com/frutas.jpg", category.getImageUrl());
        assertEquals(1, category.getSortOrder());
        assertTrue(category.getActive());
        assertNotNull(category.getCreatedAt());
    }

    @Test
    void testIsValid() {
        assertTrue(category.isValid());
        
        // Test con nombre nulo
        category.setName(null);
        assertFalse(category.isValid());
        
        // Test con nombre vacío
        category.setName("");
        assertFalse(category.isValid());
        
        // Test con nombre solo espacios
        category.setName("   ");
        assertFalse(category.isValid());
        
        // Restaurar nombre y test con descripción nula
        category.setName("Frutas");
        category.setDescription(null);
        assertFalse(category.isValid());
        
        // Test con descripción vacía
        category.setDescription("");
        assertFalse(category.isValid());
        
        // Test con descripción solo espacios
        category.setDescription("   ");
        assertFalse(category.isValid());
    }

    @Test
    void testUpdateTimestamp() {
        Timestamp originalTimestamp = category.getUpdatedAt();
        
        // Simular pasar tiempo
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        category.updateTimestamp();
        
        if (originalTimestamp != null) {
            assertNotEquals(originalTimestamp, category.getUpdatedAt());
        } else {
            assertNotNull(category.getUpdatedAt());
        }
    }

    @Test
    void testGenerateSlugFromName() {
        // Test con nombre normal
        category.setName("Frutas y Verduras");
        category.setSlug(null);
        category.generateSlugFromName();
        assertEquals("frutas-y-verduras", category.getSlug());
        
        // Test con caracteres especiales
        category.setName("Bebidas & Jugos Naturales!");
        category.setSlug(null);
        category.generateSlugFromName();
        assertEquals("bebidas-jugos-naturales", category.getSlug());
        
        // Test con espacios múltiples
        category.setName("  Carnes   Rojas  ");
        category.setSlug(null);
        category.generateSlugFromName();
        assertEquals("carnes-rojas", category.getSlug());
        
        // Test con números
        category.setName("Categoría 123 Especial");
        category.setSlug(null);
        category.generateSlugFromName();
        assertEquals("categoria-123-especial", category.getSlug());
        
        // Test cuando ya existe slug, no debe cambiar
        String existingSlug = "slug-existente";
        category.setSlug(existingSlug);
        category.generateSlugFromName();
        assertEquals(existingSlug, category.getSlug());
        
        // Test con slug vacío, debe generar nuevo
        category.setSlug("");
        category.generateSlugFromName();
        assertEquals("categoria-123-especial", category.getSlug());
        
        // Test con slug solo espacios, debe generar nuevo
        category.setSlug("   ");
        category.generateSlugFromName();
        assertEquals("categoria-123-especial", category.getSlug());
    }

    @Test
    void testEqualsAndHashCode() {
        Category category1 = Category.builder()
                .id("same-id")
                .name("Category 1")
                .build();
        
        Category category2 = Category.builder()
                .id("same-id")
                .name("Category 2")
                .build();
        
        // Debe ser igual por ID (según @EqualsAndHashCode(of = "id"))
        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void testToString() {
        String toString = category.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Frutas"));
        assertTrue(toString.contains("cat-123"));
    }

    @Test
    void testDefaultValues() {
        Category newCategory = Category.builder()
                .name("Nueva Categoria")
                .description("Descripción")
                .build();
        
        assertTrue(newCategory.getActive()); // Default true
        assertNotNull(newCategory.getCreatedAt()); // Default Timestamp.now()
    }

    @Test
    void testAllFieldsGettersSetters() {
        Category testCategory = new Category();
        
        // Test setters and getters
        testCategory.setId("test-id");
        assertEquals("test-id", testCategory.getId());
        
        testCategory.setName("Test Name");
        assertEquals("Test Name", testCategory.getName());
        
        testCategory.setDescription("Test Description");
        assertEquals("Test Description", testCategory.getDescription());
        
        testCategory.setImageUrl("http://test.com");
        assertEquals("http://test.com", testCategory.getImageUrl());
        
        testCategory.setSlug("test-slug");
        assertEquals("test-slug", testCategory.getSlug());
        
        testCategory.setSortOrder(5);
        assertEquals(5, testCategory.getSortOrder());
        
        testCategory.setActive(false);
        assertFalse(testCategory.getActive());
        
        Timestamp testTimestamp = Timestamp.now();
        testCategory.setCreatedAt(testTimestamp);
        assertEquals(testTimestamp, testCategory.getCreatedAt());
        
        testCategory.setUpdatedAt(testTimestamp);
        assertEquals(testTimestamp, testCategory.getUpdatedAt());
    }
}