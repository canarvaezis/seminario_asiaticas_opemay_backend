package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private String username;
    private String email;
    private String[] roles;
    
    public JwtResponse(String token, String username, String email, String[] roles) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
