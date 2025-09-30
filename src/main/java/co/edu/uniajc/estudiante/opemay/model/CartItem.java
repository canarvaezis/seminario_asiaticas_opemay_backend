package co.edu.uniajc.estudiante.opemay.model;

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
@EqualsAndHashCode(of = "productId")
public class CartItem {
    
    private String productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
    
    private Timestamp updatedAt;
    
    /**
     * Calcula el subtotal del item (precio * cantidad)
     */
    public Double getSubtotal() {
        if (price == null || quantity == null) {
            return 0.0;
        }
        return price * quantity;
    }
    
    /**
     * Valida que el item tenga los datos mínimos requeridos
     */
    public boolean isValid() {
        return productId != null && !productId.trim().isEmpty() &&
               price != null && price > 0 &&
               quantity != null && quantity > 0;
    }
}