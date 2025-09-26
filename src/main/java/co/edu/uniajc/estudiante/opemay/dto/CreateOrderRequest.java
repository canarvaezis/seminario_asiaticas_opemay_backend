package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "El ID del carrito es requerido")
    private String cartId;

    @NotBlank(message = "La dirección de entrega es requerida")
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres")
    private String deliveryAddress;
    
    // Alias para compatibilidad con tests
    private String shippingAddress;

    @NotBlank(message = "El método de pago es requerido")
    private String paymentMethod;
    
    // Método para compatibilidad con tests
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
        this.deliveryAddress = shippingAddress; // También establecer deliveryAddress
    }
    
    public String getShippingAddress() {
        return shippingAddress != null ? shippingAddress : deliveryAddress;
    }
}