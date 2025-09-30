package co.edu.uniajc.estudiante.opemay.dto;

import java.util.List;

import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    
    private String id;
    private String userId;
    private List<CartItem> items;
    private Double totalAmount;
    private Integer totalItems;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean active;
}