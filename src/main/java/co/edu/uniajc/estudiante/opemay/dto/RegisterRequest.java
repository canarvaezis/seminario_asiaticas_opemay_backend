package co.edu.uniajc.estudiante.opemay.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class RegisterRequest {
    
    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 3, max = 20, message = "El username debe tener entre 3 y 20 caracteres")
    private String username;
    
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;
    
    @NotBlank(message = "El password no puede estar vacío")
    @Size(min = 6, message = "El password debe tener al menos 6 caracteres")
    private String password;
    
    private String firstName;
    private String lastName;
    
    @Builder.Default
    private List<String> roles = List.of("USER");
}
