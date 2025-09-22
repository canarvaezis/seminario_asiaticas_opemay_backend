package co.edu.uniajc.estudiante.opemayfruitshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String productId;
    private String name;
    private double price;
    private int quantity;
}
