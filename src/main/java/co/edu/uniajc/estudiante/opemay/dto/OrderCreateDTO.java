package co.edu.uniajc.estudiante.opemay.dto;

import co.edu.uniajc.estudiante.opemay.model.OrderStatus;
import co.edu.uniajc.estudiante.opemay.model.PaymentStatus;
import java.util.List;
import java.util.Map;

/**
 * DTO para crear un pedido desde el carrito
 */
public class OrderCreateDTO {
    private String userId;
    private List<Map<String, Object>> cartItems; // Items del carrito
    private String shippingAddress;
    private String paymentMethod;
    private OrderStatus status = OrderStatus.PENDING;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // Constructors
    public OrderCreateDTO() {}

    public OrderCreateDTO(String userId, List<Map<String, Object>> cartItems, 
                         String shippingAddress, String paymentMethod) {
        this.userId = userId;
        this.cartItems = cartItems;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Map<String, Object>> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<Map<String, Object>> cartItems) {
        this.cartItems = cartItems;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}