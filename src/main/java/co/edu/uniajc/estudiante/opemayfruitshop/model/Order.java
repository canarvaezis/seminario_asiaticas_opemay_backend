package co.edu.uniajc.estudiante.opemayfruitshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String id;
    private String userId;
    private String createdAt;
    private String status;
    private double total;
    private List<OrderItem> items;
}
