package co.edu.uniajc.estudiante.opemay.model;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Order {
    
    private String id;
    private String userId;
    private String userEmail;
    private String userName;
    
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    private Double totalAmount;
    private Integer totalItems;
    private Double shippingCost;
    private Double discountAmount;
    private String discountCode;
    
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    
    // Información de entrega
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryPhone;
    private String deliveryInstructions;
    
    // Información de pago
    private String paymentMethod; // CASH, CARD, TRANSFER
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private String paymentReference;
    
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
    
    private Timestamp updatedAt;
    private Timestamp deliveryDate;
    private Timestamp confirmedAt;
    private Timestamp shippedAt;
    private Timestamp deliveredAt;
    
    @Builder.Default
    private Boolean active = true;
    
    /**
     * Calcula el total de la orden
     */
    public void calculateTotals() {
        if (items == null || items.isEmpty()) {
            this.totalAmount = 0.0;
            this.totalItems = 0;
            return;
        }
        
        double itemsTotal = items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
            
        this.totalItems = items.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
        
        double shipping = shippingCost != null ? shippingCost : 0.0;
        double discount = discountAmount != null ? discountAmount : 0.0;
        
        this.totalAmount = itemsTotal + shipping - discount;
    }
    
    /**
     * Verifica si la orden puede ser modificada
     */
    public boolean canBeModified() {
        return "PENDING".equals(status);
    }
    
    /**
     * Actualiza el estado de la orden con enum
     */
    public void setStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Timestamp.now();
    }
    
    /**
     * Actualiza el estado de la orden con String
     */
    public void setStatus(String newStatus) {
        this.status = OrderStatus.valueOf(newStatus);
        this.updatedAt = Timestamp.now();
    }

    public void updateStatus(String newStatus) {
        setStatus(newStatus);
        this.updatedAt = Timestamp.now();
    }

    public boolean canBeCancelled() {
        return List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PROCESSING).contains(this.status);
    }
}