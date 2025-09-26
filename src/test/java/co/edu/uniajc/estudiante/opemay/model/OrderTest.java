package co.edu.uniajc.estudiante.opemay.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId("order-123");
        order.setUserId("user-123");
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setActive(true);
        order.setPaymentMethod("CREDIT_CARD");
        order.setPaymentStatus("PENDING");
        order.setDeliveryAddress("123 Test Street");

        // Agregar items de prueba
        OrderItem item1 = new OrderItem();
        item1.setId("item-1");
        item1.setProductId("product-1");
        item1.setProductName("Producto 1");
        item1.setQuantity(2);
        item1.setPrice(10.00);

        OrderItem item2 = new OrderItem();
        item2.setId("item-2");
        item2.setProductId("product-2");
        item2.setProductName("Producto 2");
        item2.setQuantity(1);
        item2.setPrice(15.00);

        order.getItems().add(item1);
        order.getItems().add(item2);
    }

    @Test
    void testCalculateTotals() {
        order.calculateTotals();

        assertEquals(35.00, order.getSubtotal(), 0.01);
        assertEquals(3.15, order.getTax(), 0.01); // 9% de 35
        assertEquals(38.15, order.getTotalAmount(), 0.01);
    }

    @Test
    void testCanBeCancelled() {
        // Estados que permiten cancelación
        order.setStatus("PENDING");
        assertTrue(order.canBeCancelled());

        order.setStatus("CONFIRMED");
        assertTrue(order.canBeCancelled());

        order.setStatus("PROCESSING");
        assertTrue(order.canBeCancelled());

        // Estados que NO permiten cancelación
        order.setStatus("SHIPPED");
        assertFalse(order.canBeCancelled());

        order.setStatus("DELIVERED");
        assertFalse(order.canBeCancelled());

        order.setStatus("CANCELLED");
        assertFalse(order.canBeCancelled());
    }

    @Test
    void testUpdateStatus() {
        String newStatus = "CONFIRMED";
        order.updateStatus(newStatus);

        assertEquals(newStatus, order.getStatus());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void testIsValid() {
        assertTrue(order.isValid());

        // Orden sin items no es válida
        order.getItems().clear();
        assertFalse(order.isValid());

        // Restaurar items pero quitar user ID
        OrderItem item = new OrderItem();
        item.setId("item-1");
        item.setProductId("product-1");
        item.setProductName("Producto 1");
        item.setQuantity(2);
        item.setPrice(10.00);
        order.getItems().add(item);

        order.setUserId(null);
        assertFalse(order.isValid());

        order.setUserId("");
        assertFalse(order.isValid());
    }

    @Test
    void testGetTotalItems() {
        int totalItems = order.getTotalItems();
        assertEquals(3, totalItems); // 2 + 1 del setUp
    }
}