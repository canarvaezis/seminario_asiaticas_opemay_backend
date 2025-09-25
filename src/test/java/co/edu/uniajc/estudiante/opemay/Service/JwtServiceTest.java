package co.edu.uniajc.estudiante.opemay.Service;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

/**
 * Tests unitarios para JwtService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private String validToken;
    private String expiredToken;
    private String malformedToken;
    private String testUsername;

    @BeforeEach
    void setUp() {
        testUsername = "testuser";
        malformedToken = "invalid.jwt.token";
        
        // Configurar propiedades del servicio
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "opemaySecretKeyForJWTTokenGeneration2024SecureKeyLongEnough");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L); // 1 hora

        // Generar token válido
        validToken = jwtService.generateTokenFromUsername(testUsername);
        
        // Para el token expirado, configuramos una expiración muy corta temporalmente
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 1L); // 1 ms
        expiredToken = jwtService.generateTokenFromUsername(testUsername);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L); // Restaurar
        
        // Esperar un poco para que expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Nested
    @DisplayName("generateTokenFromUsername Tests")
    class GenerateTokenFromUsernameTests {

        @Test
        @DisplayName("Debe generar token válido para username")
        void shouldGenerateValidTokenForUsername() {
            // Act
            String token = jwtService.generateTokenFromUsername(testUsername);

            // Assert
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT tiene 3 partes separadas por puntos
        }

        @Test
        @DisplayName("Debe incluir username en el token")
        void shouldIncludeUsernameInToken() {
            // Act
            String token = jwtService.generateTokenFromUsername(testUsername);
            String extractedUsername = jwtService.getUsernameFromToken(token);

            // Assert
            assertThat(extractedUsername).isEqualTo(testUsername);
        }

        @Test
        @DisplayName("Debe configurar fecha de expiración correcta")
        void shouldSetCorrectExpirationDate() {
            // Act
            String token = jwtService.generateTokenFromUsername(testUsername);
            Date expiration = jwtService.getExpirationDateFromToken(token);

            // Assert
            assertThat(expiration).isAfter(new Date());
            // Verificar que expira aproximadamente en 1 hora
            long timeDiff = expiration.getTime() - System.currentTimeMillis();
            assertThat(timeDiff).isBetween(3590000L, 3610000L); // Entre 59:50 y 60:10 min
        }

        @Test
        @DisplayName("Debe generar tokens únicos para diferentes usernames")
        void shouldGenerateUniqueTokensForDifferentUsernames() {
            // Act
            String token1 = jwtService.generateTokenFromUsername("user1");
            String token2 = jwtService.generateTokenFromUsername("user2");

            // Assert
            assertThat(token1).isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("getUsernameFromToken Tests")
    class GetUsernameFromTokenTests {

        @Test
        @DisplayName("Debe extraer username correctamente")
        void shouldExtractUsernameCorrectly() {
            // Act
            String username = jwtService.getUsernameFromToken(validToken);

            // Assert
            assertThat(username).isEqualTo(testUsername);
        }

        @Test
        @DisplayName("Debe lanzar excepción con token malformado")
        void shouldThrowExceptionWithMalformedToken() {
            // Act & Assert
            assertThrows(MalformedJwtException.class, () -> jwtService.getUsernameFromToken(malformedToken));
        }

        @Test
        @DisplayName("Debe lanzar excepción con token expirado")
        void shouldThrowExceptionWithExpiredToken() {
            // Act & Assert
            assertThrows(ExpiredJwtException.class, () -> jwtService.getUsernameFromToken(expiredToken));
        }

        @Test
        @DisplayName("Debe lanzar excepción con token null")
        void shouldThrowExceptionWithNullToken() {
            // Act & Assert
            assertThrows(Exception.class, () -> jwtService.getUsernameFromToken(null));
        }

        @Test
        @DisplayName("Debe lanzar excepción con token vacío")
        void shouldThrowExceptionWithEmptyToken() {
            // Act & Assert
            assertThrows(Exception.class, () -> jwtService.getUsernameFromToken(""));
        }
    }

    @Nested
    @DisplayName("validateToken Tests")
    class ValidateTokenTests {

        @Test
        @DisplayName("Debe validar token válido correctamente")
        void shouldValidateValidTokenCorrectly() {
            // Act
            boolean isValid = jwtService.validateToken(validToken);

            // Assert
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Debe rechazar token malformado")
        void shouldRejectMalformedToken() {
            // Act
            boolean isValid = jwtService.validateToken(malformedToken);

            // Assert
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Debe rechazar token expirado")
        void shouldRejectExpiredToken() {
            // Act
            boolean isValid = jwtService.validateToken(expiredToken);

            // Assert
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Debe rechazar token null")
        void shouldRejectNullToken() {
            // Act
            boolean isValid = jwtService.validateToken(null);

            // Assert
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Debe rechazar token vacío")
        void shouldRejectEmptyToken() {
            // Act
            boolean isValid = jwtService.validateToken("");

            // Assert
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Debe rechazar token con firma incorrecta")
        void shouldRejectTokenWithWrongSignature() {
            // Arrange - crear un token con diferente clave
            JwtService differentJwtService = new JwtService();
            ReflectionTestUtils.setField(differentJwtService, "jwtSecret", "differentSecretKey123456789012345678901234567890");
            ReflectionTestUtils.setField(differentJwtService, "jwtExpirationMs", 3600000L);
            String tokenWithDifferentSignature = differentJwtService.generateTokenFromUsername(testUsername);

            // Act & Assert - Debe manejar la excepción SignatureException
            try {
                boolean isValid = jwtService.validateToken(tokenWithDifferentSignature);
                assertFalse(isValid);
            } catch (io.jsonwebtoken.security.SignatureException e) {
                // Esta excepción es esperada cuando la firma no es válida
                assertTrue(true);
            }
        }
    }

    @Nested
    @DisplayName("getExpirationDateFromToken Tests")
    class GetExpirationDateFromTokenTests {

        @Test
        @DisplayName("Debe extraer fecha de expiración correctamente")
        void shouldExtractExpirationCorrectly() {
            // Act
            Date expiration = jwtService.getExpirationDateFromToken(validToken);

            // Assert
            assertThat(expiration).isNotNull();
            assertThat(expiration).isAfter(new Date());
        }

        @Test
        @DisplayName("Debe lanzar excepción con token malformado")
        void shouldThrowExceptionWithMalformedTokenForExpiration() {
            // Act & Assert
            assertThrows(MalformedJwtException.class, () -> jwtService.getExpirationDateFromToken(malformedToken));
        }

        @Test
        @DisplayName("Debe lanzar excepción con token expirado")
        void shouldThrowExceptionWithExpiredTokenForExpiration() {
            // Act & Assert
            assertThrows(ExpiredJwtException.class, () -> jwtService.getExpirationDateFromToken(expiredToken));
        }
    }

    @Nested
    @DisplayName("isTokenExpired Tests")
    class IsTokenExpiredTests {

        @Test
        @DisplayName("Token válido no debe estar expirado")
        void validTokenShouldNotBeExpired() {
            // Act
            boolean isExpired = jwtService.isTokenExpired(validToken);

            // Assert
            assertFalse(isExpired);
        }

        @Test
        @DisplayName("Token expirado debe estar marcado como expirado")
        void expiredTokenShouldBeMarkedAsExpired() {
            // Act & Assert
            assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(expiredToken));
        }

        @Test
        @DisplayName("Debe lanzar excepción con token malformado")
        void shouldThrowExceptionWithMalformedTokenForExpiredCheck() {
            // Act & Assert
            assertThrows(MalformedJwtException.class, () -> jwtService.isTokenExpired(malformedToken));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Flujo completo: generar, validar y extraer información")
        void completeFlowGenerateValidateAndExtract() {
            // Arrange
            String username = "integrationtest";

            // Act - Generar token
            String token = jwtService.generateTokenFromUsername(username);
            
            // Act - Validar token
            boolean isValid = jwtService.validateToken(token);
            
            // Act - Extraer información
            String extractedUsername = jwtService.getUsernameFromToken(token);
            Date expiration = jwtService.getExpirationDateFromToken(token);
            boolean isExpired = jwtService.isTokenExpired(token);

            // Assert
            assertThat(token).isNotNull();
            assertTrue(isValid);
            assertThat(extractedUsername).isEqualTo(username);
            assertThat(expiration).isAfter(new Date());
            assertFalse(isExpired);
        }

        @Test
        @DisplayName("Debe manejar múltiples operaciones con el mismo token")
        void shouldHandleMultipleOperationsWithSameToken() {
            // Arrange
            String token = jwtService.generateTokenFromUsername("multipleops");

            // Act & Assert - Múltiples validaciones deben funcionar
            assertTrue(jwtService.validateToken(token));
            assertTrue(jwtService.validateToken(token));
            assertTrue(jwtService.validateToken(token));

            // Act & Assert - Múltiples extracciones deben funcionar
            assertThat(jwtService.getUsernameFromToken(token)).isEqualTo("multipleops");
            assertThat(jwtService.getUsernameFromToken(token)).isEqualTo("multipleops");
            assertThat(jwtService.getUsernameFromToken(token)).isEqualTo("multipleops");
        }
    }
}