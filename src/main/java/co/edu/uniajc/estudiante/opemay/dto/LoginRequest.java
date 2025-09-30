package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})  // Excluir password del toString por seguridad
public class LoginRequest {
    
    @NotBlank(message = "El username no puede estar vacío")
    private String username;
    
    @NotBlank(message = "El password no puede estar vacío")
    private String password;

    @NotBlank(message = "El email no puede estar vacío")
    private String email;
}
