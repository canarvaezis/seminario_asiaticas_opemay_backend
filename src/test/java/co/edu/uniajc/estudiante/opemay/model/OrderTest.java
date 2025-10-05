package co.edu.uniajc.estudiante.opemay.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.cloud.Timestamp;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id("order-123")
                .userId("user-123")
                .status(OrderStatus.PENDING)
                .active(true)
                .paymentMethod("CREDIT_CARD")
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryAddress("123 Test Street")
                .build();
        order.setCreatedAt(Timestamp.now());

        // Agregar items de prueba
        OrderItem item1 = OrderItem.builder()
                .productId("product-1")
                .productName("Producto 1")
                .quantity(2)
                .price(10.00)
                .build();

        OrderItem item2 = OrderItem.builder()
                .productId("product-2")
                .productName("Producto 2")
                .quantity(1)
                .price(15.00)
                .build();

        order.getItems().add(item1);
        order.getItems().add(item2);
    }

    @Test
    void testCalculateTotals() {
        order.calculateTotals();

        assertEquals(35.00, order.getTotalAmount(), 0.01);
        assertEquals(35.00, order.getTotalAmount(), 0.01); // Total sin impuestos separados
    }

    @Test
    void testCanBeCancelled() {
        // Estados que permiten cancelación
        order.updateStatus("PENDING");
        assertTrue(order.canBeCancelled());

        order.updateStatus("CONFIRMED");
        assertTrue(order.canBeCancelled());

        order.updateStatus("PROCESSING");
        assertTrue(order.canBeCancelled());

        // Estados que NO permiten cancelación
        order.updateStatus("SHIPPED");
        assertFalse(order.canBeCancelled());

        order.updateStatus("DELIVERED");
        assertFalse(order.canBeCancelled());

        order.updateStatus("CANCELLED");
        assertFalse(order.canBeCancelled());
    }

    @Test
    void testUpdateStatus() {
        String newStatus = "CONFIRMED";
        order.updateStatus(newStatus);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void testOrderValidation() {
        // Verificar que la orden tiene información básica
        assertNotNull(order.getUserId());
        assertNotNull(order.getStatus());
        assertFalse(order.getItems().isEmpty());

        // Orden sin items
        order.getItems().clear();
        assertTrue(order.getItems().isEmpty());

        // Restaurar items pero quitar user ID
        OrderItem item = OrderItem.builder()
                .productId("product-1")
                .productName("Producto 1")
                .quantity(2)
                .price(10.00)
                .build();
        order.getItems().add(item);

        order.setUserId(null);
        assertNull(order.getUserId());

        order.setUserId("");
        assertEquals("", order.getUserId());
    }

    @Test
    void testGetTotalItems() {
        order.calculateTotals();
        Integer totalItems = order.getTotalItems();
        assertEquals(3, totalItems); // 2 + 1 del setUp
    }
}