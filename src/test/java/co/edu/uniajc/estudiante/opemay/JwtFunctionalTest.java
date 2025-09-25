package co.edu.uniajc.estudiante.opemay;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import co.edu.uniajc.estudiante.opemay.config.TestConfig;

import co.edu.uniajc.estudiante.opemay.Service.JwtService;

/**
 * Pruebas funcionales simples para JWT
 */
@SpringBootTest(classes = OpemayApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "firebase.enabled=false"
})
@Import(TestConfig.class)
public class JwtFunctionalTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void contextLoadsWithJwtService() {
        assertThat(jwtService).isNotNull();
    }

    @Test
    void jwtServiceCanGenerateAndValidateTokens() {
        String username = "testuser";
        
        // Generar token
        String token = jwtService.generateTokenFromUsername(username);
        
        // Verificaciones básicas
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        // Validar token
        boolean isValid = jwtService.validateToken(token);
        assertThat(isValid).isTrue();
        
        // Extraer username
        String extractedUsername = jwtService.getUsernameFromToken(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void differentUsersGenerateDifferentTokens() {
        String token1 = jwtService.generateTokenFromUsername("user1");
        String token2 = jwtService.generateTokenFromUsername("user2");
        
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtService.validateToken(token1)).isTrue();
        assertThat(jwtService.validateToken(token2)).isTrue();
    }
}