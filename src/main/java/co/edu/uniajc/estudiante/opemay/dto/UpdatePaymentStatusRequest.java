package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatePaymentStatusRequest {

    @NotBlank(message = "El estado de pago es requerido")
    @Pattern(
        regexp = "PENDING|PROCESSING|COMPLETED|FAILED|REFUNDED",
        message = "Estado de pago no válido"
    )
    private String paymentStatus;
}