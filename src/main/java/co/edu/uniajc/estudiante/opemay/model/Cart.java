package co.edu.uniajc.estudiante.opemay.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private String productId;
    private int quantity;
    private double price;
    private String name;        // <- agregado
    private String description; // <- agregado
    private Date createdAt;
    private Date updatedAt;
}
