package co.edu.uniajc.estudiante.opemay.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.config.JacksonConfig.TimestampDeserializer;
import co.edu.uniajc.estudiante.opemay.config.JacksonConfig.TimestampSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @JsonSerialize(using = TimestampSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp createdAt = Timestamp.now();
    
    @JsonSerialize(using = TimestampSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp updatedAt;
    
    @JsonSerialize(using = TimestampSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp deliveryDate;
    
    @JsonSerialize(using = TimestampSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp confirmedAt;
    
    @JsonSerialize(using = TimestampSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp shippedAt;
    
    @JsonSerialize(using = TimestampSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
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