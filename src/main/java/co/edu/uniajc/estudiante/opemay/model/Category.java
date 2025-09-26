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
@EqualsAndHashCode(of = "id")
public class Category {
    
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String slug; // URL friendly name
    private Integer sortOrder; // Para ordenamiento personalizado
    
    @Builder.Default
    private Boolean active = true;
    
    @Builder.Default
    private Timestamp createdAt = Timestamp.now();
    
    private Timestamp updatedAt;
    
    /**
     * Valida que la categoría tenga los datos mínimos requeridos
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               description != null && !description.trim().isEmpty();
    }
    
    /**
     * Actualiza el timestamp de modificación
     */
    public void updateTimestamp() {
        this.updatedAt = Timestamp.now();
    }
    
    /**
     * Genera un slug basado en el nombre si no existe
     */
    public void generateSlugFromName() {
        if ((this.slug == null || this.slug.trim().isEmpty()) && this.name != null) {
            this.slug = this.name
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        }
    }
}