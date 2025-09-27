package co.edu.uniajc.estudiante.opemay.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

//     @Test
//     void testGenerateSlugFromName() {
//         // Test con nombre simple
//         String result1 = Category.generateSlugFromName("Frutas");
//         assertNotNull(result1);
//         assertTrue(result1.toLowerCase().contains("fruta") || result1.equals("frutas"));
//
//         // Test con espacios
//         String result2 = Category.generateSlugFromName("Frutas Frescas");
//         assertNotNull(result2);
//         assertFalse(result2.contains(" "));
//
//         // Test con caracteres especiales
//         String result3 = Category.generateSlugFromName("Frutas & Verduras");
//         assertNotNull(result3);
//         assertFalse(result3.contains("&"));
//
//         // Test con acentos
//         String result4 = Category.generateSlugFromName("Lácteos");
//         assertNotNull(result4);
//         assertFalse(result4.contains("á"));
//
//         // Test con múltiples espacios
//         String result5 = Category.generateSlugFromName("Productos   Orgánicos");
//         assertNotNull(result5);
//         assertFalse(result5.contains("   "));
//
//         // Test con null
//         assertNull(Category.generateSlugFromName(null));
//
//         // Test con string vacío
//         assertEquals("", Category.generateSlugFromName(""));
//
//         // Test con solo espacios
//         assertEquals("", Category.generateSlugFromName("   "));
//     }

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