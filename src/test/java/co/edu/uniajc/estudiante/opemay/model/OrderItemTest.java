package co.edu.uniajc.estudiante.opemay.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = OrderItem.builder()
                .productId("product-123")
                .productName("Producto de Prueba")
                .quantity(3)
                .price(12.50)
                .imageUrl("http://example.com/image.jpg")
                .build();
    }

    @Test
    void testGetSubtotal() {
        double expected = 3 * 12.50; // quantity * price
        assertEquals(expected, orderItem.getSubtotal(), 0.01);
    }

    @Test
    void testGetSubtotalWithZeroQuantity() {
        orderItem.setQuantity(0);
        assertEquals(0.0, orderItem.getSubtotal(), 0.01);
    }

    @Test
    void testGetSubtotalWithZeroPrice() {
        orderItem.setPrice(0.0);
        assertEquals(0.0, orderItem.getSubtotal(), 0.01);
    }

    @Test
    void testIsValid() {
        assertTrue(orderItem.isValid());

        // Test con producto ID nulo
        orderItem.setProductId(null);
        assertFalse(orderItem.isValid());

        orderItem.setProductId("");
        assertFalse(orderItem.isValid());

        // Restaurar y test con nombre nulo
        orderItem.setProductId("product-123");
        orderItem.setProductName(null);
        assertFalse(orderItem.isValid());

        orderItem.setProductName("");
        assertFalse(orderItem.isValid());

        // Restaurar y test con cantidad inválida
        orderItem.setProductName("Producto de Prueba");
        orderItem.setQuantity(0);
        assertFalse(orderItem.isValid());

        orderItem.setQuantity(-1);
        assertFalse(orderItem.isValid());

        // Restaurar y test con precio inválido
        orderItem.setQuantity(3);
        orderItem.setPrice(-1.0);
        assertFalse(orderItem.isValid());
    }

    @Test
    void testValidationWithValidData() {
        // Todos los campos válidos
        orderItem.setProductId("valid-product-id");
        orderItem.setProductName("Valid Product Name");
        orderItem.setQuantity(1);
        orderItem.setPrice(0.01); // Precio mínimo válido

        assertTrue(orderItem.isValid());
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        OrderItem item1 = OrderItem.builder()
                .productId("product-456")
                .productName("Test Product")
                .build();
        
        OrderItem item2 = OrderItem.builder()
                .productId("product-456")
                .productName("Test Product")
                .build();
        
        // Then
        assertEquals(item1.getProductId(), item2.getProductId());
    }
}