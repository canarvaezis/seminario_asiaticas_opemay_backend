package co.edu.uniajc.estudiante.opemay.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.IRespository.CartRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.OrderRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductRepository;
import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.model.CartItem;
import co.edu.uniajc.estudiante.opemay.model.Order;
import co.edu.uniajc.estudiante.opemay.model.OrderStatus;
import co.edu.uniajc.estudiante.opemay.model.Product;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Cart testCart;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Setup test cart
        testCart = new Cart();
        testCart.setId("cart-123");
        testCart.setUserId("user-123");
        testCart.setActive(true);
        testCart.setCreatedAt(Timestamp.now());

        // Setup cart item
        CartItem cartItem = CartItem.builder()
                .productId("product-123")
                .productName("Test Product")
                .quantity(2)
                .price(25.00)
                .build();
        
        testCart.getItems().add(cartItem);

        // Setup test product
        testProduct = new Product();
        testProduct.setId("product-123");
        testProduct.setName("Test Product");
        testProduct.setPrice(25.00);
        testProduct.setStock(10);
        testProduct.setActive(true);
        testProduct.setImageUrl("http://example.com/image.jpg");

        // Setup test order
        testOrder = new Order();
        testOrder.setId("order-123");
        testOrder.setUserId("user-123");
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setCreatedAt(Timestamp.now());
        testOrder.setActive(true);
    }

    @Test
    void testCreateOrderFromCart_Success() throws ExecutionException, InterruptedException {
        // Given
        when(cartRepository.getCartById("cart-123")).thenReturn(testCart);
        when(productRepository.getProductById("product-123")).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn("saved");

        // When
        Order result = orderService.createOrderFromCart("cart-123", "123 Test Street", "CREDIT_CARD");

        // Then
        assertNotNull(result);
        assertEquals("user-123", result.getUserId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals("123 Test Street", result.getDeliveryAddress());
        assertEquals("CREDIT_CARD", result.getPaymentMethod());
        assertEquals(1, result.getItems().size());
        
        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(testProduct);
        verify(cartRepository).update(testCart);
    }

    @Test
    void testCreateOrderFromCart_CartNotFound() throws ExecutionException, InterruptedException {
        // Given
        when(cartRepository.getCartById("cart-123")).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrderFromCart("cart-123", "123 Test Street", "CREDIT_CARD");
        });
        
        assertEquals("Carrito no encontrado o inactivo", exception.getMessage());
    }

    @Test
    void testCreateOrderFromCart_InactiveCart() throws ExecutionException, InterruptedException {
        // Given
        testCart.setActive(false);
        when(cartRepository.getCartById("cart-123")).thenReturn(testCart);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrderFromCart("cart-123", "123 Test Street", "CREDIT_CARD");
        });
        
        assertEquals("Carrito no encontrado o inactivo", exception.getMessage());
    }

    @Test
    void testCreateOrderFromCart_EmptyCart() throws ExecutionException, InterruptedException {
        // Given
        testCart.getItems().clear();
        when(cartRepository.getCartById("cart-123")).thenReturn(testCart);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrderFromCart("cart-123", "123 Test Street", "CREDIT_CARD");
        });
        
        assertEquals("El carrito está vacío", exception.getMessage());
    }

    @Test
    void testCreateOrderFromCart_InsufficientStock() throws ExecutionException, InterruptedException {
        // Given
        testProduct.setStock(1); // Menos stock del requerido (2)
        when(cartRepository.getCartById("cart-123")).thenReturn(testCart);
        when(productRepository.getProductById("product-123")).thenReturn(testProduct);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrderFromCart("cart-123", "123 Test Street", "CREDIT_CARD");
        });
        
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
    }

    @Test
    void testUpdateOrderStatus_Success() throws ExecutionException, InterruptedException {
        // Given
        when(orderRepository.getOrderById("order-123")).thenReturn(testOrder);
        when(orderRepository.update(testOrder)).thenReturn("updated");

        // When
        Order result = orderService.updateOrderStatus("order-123", "CONFIRMED");

        // Then
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertNotNull(result.getConfirmedAt());
        verify(orderRepository).update(testOrder);
    }

    @Test
    void testUpdateOrderStatus_InvalidTransition() throws ExecutionException, InterruptedException {
        // Given
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.getOrderById("order-123")).thenReturn(testOrder);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrderStatus("order-123", "PENDING");
        });
        
        assertTrue(exception.getMessage().contains("Transición de estado no válida"));
    }

    @Test
    void testCancelOrder_Success() throws ExecutionException, InterruptedException {
        // Given
        when(orderRepository.getOrderById("order-123")).thenReturn(testOrder);
        when(orderRepository.update(testOrder)).thenReturn("updated");

        // When
        Order result = orderService.cancelOrder("order-123", "Usuario cambió de opinión");

        // Then
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        // Verificar que la orden fue actualizada
        assertNotNull(result.getUpdatedAt());
        verify(orderRepository).update(testOrder);
    }

    @Test
    void testCancelOrder_CannotBeCancelled() throws ExecutionException, InterruptedException {
        // Given
        testOrder.updateStatus("DELIVERED");
        when(orderRepository.getOrderById("order-123")).thenReturn(testOrder);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.cancelOrder("order-123", "Test reason");
        });
        
        assertTrue(exception.getMessage().contains("no puede ser cancelada"));
    }

    @Test
    void testGetOrderStats() throws ExecutionException, InterruptedException {
        // Given
        Order order1 = new Order();
        order1.updateStatus("PENDING");
        
        Order order2 = new Order();
        order2.updateStatus("DELIVERED");
        order2.setTotalAmount(100.0);
        
        Order order3 = new Order();
        order3.updateStatus("CANCELLED");
        
        List<Order> orders = Arrays.asList(order1, order2, order3);
        when(orderRepository.getAllOrders()).thenReturn(orders);

        // When
        OrderService.OrderStats stats = orderService.getOrderStats();

        // Then
        assertEquals(3, stats.getTotalOrders());
        assertEquals(1, stats.getPendingOrders());
        assertEquals(1, stats.getDeliveredOrders());
        assertEquals(1, stats.getCancelledOrders());
        assertEquals(100.0, stats.getTotalRevenue(), 0.01);
    }
}