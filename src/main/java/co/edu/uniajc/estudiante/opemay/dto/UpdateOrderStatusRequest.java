package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotBlank(message = "El estado es requerido")
    @Pattern(
        regexp = "PENDING|CONFIRMED|PROCESSING|SHIPPED|DELIVERED|CANCELLED",
        message = "Estado no válido"
    )
    private String status;

    private String reason; // Para cancelaciones
}