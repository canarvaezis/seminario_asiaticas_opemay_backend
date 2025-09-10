package co.edu.uniajc.estudiante.opemayfruitshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String id;
    private String name;
    private double price;
    private double stock;
}
