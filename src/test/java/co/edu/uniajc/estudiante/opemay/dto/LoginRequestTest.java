package co.edu.uniajc.estudiante.opemay.dto;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para LoginRequest DTO
 */
@DisplayName("LoginRequest DTO Tests")
class LoginRequestTest {

    @Test
    @DisplayName("Debe crear LoginRequest con constructor")
    void shouldCreateLoginRequestWithConstructor() {
        // Act
        LoginRequest request = new LoginRequest("testuser", "password123", "test@example.com");

        // Assert
        assertThat(request).isNotNull();
        assertThat(request.getUsername()).isEqualTo("testuser");
        assertThat(request.getPassword()).isEqualTo("password123");
        assertThat(request.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Debe permitir modificar propiedades")
    void shouldAllowModifyingProperties() {
        // Arrange
        LoginRequest request = new LoginRequest();

        // Act
        request.setUsername("newuser");
        request.setPassword("newpassword");
        request.setEmail("new@example.com");

        // Assert
        assertThat(request.getUsername()).isEqualTo("newuser");
        assertThat(request.getPassword()).isEqualTo("newpassword");
        assertThat(request.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Debe manejar valores null")
    void shouldHandleNullValues() {
        // Act
        LoginRequest request = new LoginRequest(null, null, null);

        // Assert
        assertThat(request.getUsername()).isNull();
        assertThat(request.getPassword()).isNull();
        assertThat(request.getEmail()).isNull();
    }

    @Test
    @DisplayName("Equals y HashCode deben funcionar correctamente")
    void equalsAndHashCodeShouldWorkCorrectly() {
        // Arrange
        LoginRequest request1 = new LoginRequest("user", "pass", "user@example.com");
        LoginRequest request2 = new LoginRequest("user", "pass", "user@example.com");
        LoginRequest request3 = new LoginRequest("different", "pass", "different@example.com");

        // Assert
        assertThat(request1).isEqualTo(request2);
        assertThat(request1).isNotEqualTo(request3);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("ToString debe contener información relevante")
    void toStringShouldContainRelevantInfo() {
        // Arrange
        LoginRequest request = new LoginRequest("testuser", "password123", "test@example.com");

        // Act
        String requestString = request.toString();

        // Assert
        assertThat(requestString).contains("testuser");
        assertThat(requestString).contains("test@example.com");
        // No debe mostrar la contraseña por seguridad (si está implementado así)
    }
}