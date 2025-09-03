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
    
    private String category;
    private Integer stock;
    private String imageUrl;
}
