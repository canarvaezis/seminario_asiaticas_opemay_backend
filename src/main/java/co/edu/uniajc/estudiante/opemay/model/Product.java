package co.edu.uniajc.estudiante.opemay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import com.google.cloud.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Product {
    
    private String id;
    private String name;
    private Double price;
    private String description;
    
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
    
    private Timestamp updatedAt;
    
    @Builder.Default
    private Boolean active = true;
    
    private String categoryId;
    private String categoryName; // Desnormalizado para consultas más rápidas
    private Integer stock;
    private String imageUrl;
    private String unit; // kg, unidad, litro, etc.
    private Double weight; // peso en gramos
    private String origin; // origen del producto
    
    /**
     * Valida que el producto tenga los datos mínimos requeridos
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               price != null && price > 0;
    }
    
    /**
     * Verifica si el producto está disponible para la venta
     */
    public boolean isAvailable() {
        return active && (stock == null || stock > 0);
    }
}
