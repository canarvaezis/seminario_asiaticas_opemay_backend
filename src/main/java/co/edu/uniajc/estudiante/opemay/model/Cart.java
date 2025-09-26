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
public class Cart {
    
    private String id;
    private String userId;
    
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    @Builder.Default
    private Double totalAmount = 0.0;
    
    @Builder.Default
    private Integer totalItems = 0;
    
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, ABANDONED
    
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
    
    private Timestamp updatedAt;
    
    @Builder.Default
    private Boolean active = true;
    
    /**
     * Calcula el monto total del carrito
     */
    public void calculateTotals() {
        if (items == null || items.isEmpty()) {
            this.totalAmount = 0.0;
            this.totalItems = 0;
            return;
        }
        
        this.totalAmount = items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
            
        this.totalItems = items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
    
    /**
     * Agrega un item al carrito o actualiza la cantidad si ya existe
     */
    public void addItem(CartItem newItem) {
        if (items == null) {
            items = new ArrayList<>();
        }
        
        CartItem existingItem = items.stream()
            .filter(item -> item.getProductId().equals(newItem.getProductId()))
            .findFirst()
            .orElse(null);
            
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
            existingItem.setUpdatedAt(Timestamp.now());
        } else {
            items.add(newItem);
        }
        
        calculateTotals();
        this.updatedAt = Timestamp.now();
    }
    
    /**
     * Remueve un item del carrito
     */
    public void removeItem(String productId) {
        if (items != null) {
            items.removeIf(item -> item.getProductId().equals(productId));
            calculateTotals();
            this.updatedAt = Timestamp.now();
        }
    }
    
    /**
     * Limpia todos los items del carrito
     */
    public void clearCart() {
        if (items != null) {
            items.clear();
        }
        calculateTotals();
        this.updatedAt = Timestamp.now();
    }
}