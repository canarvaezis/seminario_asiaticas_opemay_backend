package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
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

import co.edu.uniajc.estudiante.opemay.Service.OrderService;
import co.edu.uniajc.estudiante.opemay.Service.UserService;
import co.edu.uniajc.estudiante.opemay.config.TestFirebaseConfig;
import co.edu.uniajc.estudiante.opemay.config.TestSecurityConfig;
import co.edu.uniajc.estudiante.opemay.dto.CreateOrderRequest;
import co.edu.uniajc.estudiante.opemay.dto.OrderCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.UpdateOrderStatusRequest;
import co.edu.uniajc.estudiante.opemay.dto.UpdatePaymentStatusRequest;
import co.edu.uniajc.estudiante.opemay.model.Order;
import co.edu.uniajc.estudiante.opemay.model.OrderStatus;
import co.edu.uniajc.estudiante.opemay.model.PaymentStatus;
import co.edu.uniajc.estudiante.opemay.model.User;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
@Import({TestFirebaseConfig.class, TestSecurityConfig.class})
@SuppressWarnings("deprecation") // Suppress MockBean deprecation warnings
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @MockBean  
    private co.edu.uniajc.estudiante.opemay.Service.JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private List<Order> testOrders;
    private User testUser;
    private CreateOrderRequest createOrderRequest;
    private UpdateOrderStatusRequest updateStatusRequest;
    private UpdatePaymentStatusRequest updatePaymentRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .username("testuser")
                .email("test@example.com")
                .enabled(true)
                .build();

        testOrder = Order.builder()
                .id("order-123")
                .userId("user-123")
                .userEmail("test@example.com")
                .totalAmount(99.99)
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Order order2 = Order.builder()
                .id("order-456")
                .userId("user-123")
                .userEmail("test@example.com")
                .totalAmount(149.50)
                .status(OrderStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PAID)
                .build();

        testOrders = Arrays.asList(testOrder, order2);

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCartId("cart-123");
        createOrderRequest.setShippingAddress("123 Test Street");
        createOrderRequest.setPaymentMethod("CREDIT_CARD");

        updateStatusRequest = new UpdateOrderStatusRequest();
        updateStatusRequest.setStatus("CONFIRMED");

        updatePaymentRequest = new UpdatePaymentStatusRequest();
        updatePaymentRequest.setPaymentStatus("PAID");
    }

    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void testCreateOrder_Success() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(orderService.createOrderFromCart(eq("cart-123"), eq("user-123"), any(CreateOrderRequest.class)))
                .thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("order-123"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.totalAmount").value(99.99))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(userService).getUserByUsername("testuser");
        verify(orderService).createOrderFromCart(eq("cart-123"), eq("user-123"), any(CreateOrderRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void testCreateOrder_UserNotFound() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).getUserByUsername("testuser");
        verify(orderService, never()).createOrderFromCart(anyString(), anyString(), any(CreateOrderRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void testCreateOrder_ExecutionException() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(orderService.createOrderFromCart(anyString(), anyString(), any(CreateOrderRequest.class)))
                .thenThrow(new ExecutionException("Database error", new RuntimeException()));

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).getUserByUsername("testuser");
        verify(orderService).createOrderFromCart(anyString(), anyString(), any(CreateOrderRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void testGetMyOrders_Success() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(orderService.getUserOrders("user-123")).thenReturn(testOrders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/my-orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("order-123"))
                .andExpect(jsonPath("$[1].id").value("order-456"));

        verify(userService).getUserByUsername("testuser");
        verify(orderService).getUserOrders("user-123");
    }

    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void testGetOrderById_Success() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(orderService.getOrderById("order-123")).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(get("/api/orders/order-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order-123"))
                .andExpect(jsonPath("$.userId").value("user-123"));

        verify(userService).getUserByUsername("testuser");
        verify(orderService).getOrderById("order-123");
    }

    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void testGetOrderById_NotFound() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(orderService.getOrderById("nonexistent")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/orders/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).getUserByUsername("testuser");
        verify(orderService).getOrderById("nonexistent");
    }

    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void testGetOrderById_AccessDenied() throws Exception {
        // Arrange - Order pertenece a otro usuario
        Order otherUserOrder = Order.builder()
                .id("order-999")
                .userId("other-user")
                .build();
                
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(orderService.getOrderById("order-999")).thenReturn(otherUserOrder);

        // Act & Assert
        mockMvc.perform(get("/api/orders/order-999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).getUserByUsername("testuser");
        verify(orderService).getOrderById("order-999");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllOrders_Success() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(testOrders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/admin/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderService).getAllOrders();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllOrders_AccessDenied() throws Exception {
        // Act & Assert - Solo ADMIN puede acceder
        mockMvc.perform(get("/api/orders/admin/all"))
                .andExpect(status().isForbidden());

        verify(orderService, never()).getAllOrders();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_Success() throws Exception {
        // Arrange
        Order updatedOrder = Order.builder()
                .id("order-123")
                .status(OrderStatus.CONFIRMED)
                .build();
        
        when(orderService.updateOrderStatus("order-123", OrderStatus.CONFIRMED))
                .thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/order-123/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateStatusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order-123"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderService).updateOrderStatus("order-123", OrderStatus.CONFIRMED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_NotFound() throws Exception {
        // Arrange
        when(orderService.updateOrderStatus("nonexistent", OrderStatus.CONFIRMED))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/api/orders/nonexistent/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateStatusRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(orderService).updateOrderStatus("nonexistent", OrderStatus.CONFIRMED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePaymentStatus_Success() throws Exception {
        // Arrange
        Order updatedOrder = Order.builder()
                .id("order-123")
                .paymentStatus(PaymentStatus.PAID)
                .build();
        
        when(orderService.updatePaymentStatus("order-123", PaymentStatus.PAID))
                .thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/order-123/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePaymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order-123"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        verify(orderService).updatePaymentStatus("order-123", PaymentStatus.PAID);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateOrderStatus_AccessDenied() throws Exception {
        // Act & Assert - Solo ADMIN puede actualizar estado
        mockMvc.perform(put("/api/orders/order-123/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateStatusRequest)))
                .andExpect(status().isForbidden());

        verify(orderService, never()).updateOrderStatus(anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdatePaymentStatus_AccessDenied() throws Exception {
        // Act & Assert - Solo ADMIN puede actualizar estado de pago
        mockMvc.perform(put("/api/orders/order-123/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePaymentRequest)))
                .andExpect(status().isForbidden());

        verify(orderService, never()).updatePaymentStatus(anyString(), anyString());
    }

    @Test
    void testControllerExists() {
        OrderController controller = new OrderController(orderService, userService);
        assertNotNull(controller);
    }

    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void testCreateOrder_InvalidRequest() throws Exception {
        // Arrange - request sin cartId
        CreateOrderRequest invalidRequest = new CreateOrderRequest();
        invalidRequest.setShippingAddress("123 Test Street");

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetOrdersByStatus_Success() throws Exception {
        // Arrange
        List<Order> pendingOrders = Arrays.asList(testOrder);
        when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/admin/status")
                .param("status", "PENDING")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService).getOrdersByStatus(OrderStatus.PENDING);
    }
}