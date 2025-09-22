package co.edu.uniajc.estudiante.opemayfruitshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String id;
    private String productId;
    private int quantity;
}
