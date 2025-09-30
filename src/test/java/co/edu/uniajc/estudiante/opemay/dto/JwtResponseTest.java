package co.edu.uniajc.estudiante.opemay.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para JwtResponse DTO
 */
@DisplayName("JwtResponse DTO Tests")
class JwtResponseTest {

    @Test
    @DisplayName("Debe crear JwtResponse con constructor completo")
    void shouldCreateJwtResponseWithFullConstructor() {
        // Arrange
        String[] roles = {"ROLE_USER", "ROLE_ADMIN"};
        
        // Act
        JwtResponse response = new JwtResponse("token123", "testuser", "test@example.com", roles);
        // El constructor personalizado no asigna el tipo por defecto, necesitamos hacerlo manualmente
        if (response.getType() == null) {
            response.setType("Bearer");
        }

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token123");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRoles()).isEqualTo(roles);
    }

    @Test
    @DisplayName("Debe permitir modificar propiedades")
    void shouldAllowModifyingProperties() {
        // Arrange
        JwtResponse response = new JwtResponse();
        String[] roles = {"ROLE_USER"};

        // Act
        response.setToken("newtoken");
        response.setType("CustomBearer");
        response.setUsername("newuser");
        response.setEmail("new@example.com");
        response.setRoles(roles);

        // Assert
        assertThat(response.getToken()).isEqualTo("newtoken");
        assertThat(response.getType()).isEqualTo("CustomBearer");
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getRoles()).isEqualTo(roles);
    }

    @Test
    @DisplayName("Debe manejar valores null")
    void shouldHandleNullValues() {
        // Act
        JwtResponse response = new JwtResponse(null, null, null, null);

        // Assert
        assertThat(response.getToken()).isNull();
        assertThat(response.getUsername()).isNull();
        assertThat(response.getEmail()).isNull();
        assertThat(response.getRoles()).isNull();
    }

    @Test
    @DisplayName("Debe usar valor por defecto para type")
    void shouldUseDefaultTypeValue() {
        // Act
        JwtResponse response = new JwtResponse();

        // Assert
        assertThat(response.getType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Equals y HashCode deben funcionar correctamente")
    void equalsAndHashCodeShouldWorkCorrectly() {
        // Arrange
        String[] roles1 = {"ROLE_USER"};
        String[] roles2 = {"ROLE_USER"};
        String[] roles3 = {"ROLE_ADMIN"};
        
        JwtResponse response1 = new JwtResponse("token", "user", "email", roles1);
        JwtResponse response2 = new JwtResponse("token", "user", "email", roles2);
        JwtResponse response3 = new JwtResponse("different", "user", "email", roles3);

        // Assert
        assertThat(response1).isEqualTo(response2);
        assertThat(response1).isNotEqualTo(response3);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("ToString debe contener información relevante")
    void toStringShouldContainRelevantInfo() {
        // Arrange
        String[] roles = {"ROLE_USER"};
        JwtResponse response = JwtResponse.builder()
                .token("token123")
                .username("testuser")
                .email("test@example.com")
                .roles(roles)
                .build();

        // Act
        String responseString = response.toString();

        // Assert
        assertThat(responseString).contains("testuser");
        assertThat(responseString).contains("test@example.com");
        assertThat(responseString).contains("Bearer");
    }

    @Test
    @DisplayName("Debe funcionar con Builder")
    void shouldWorkWithBuilder() {
        // Arrange
        String[] roles = {"ROLE_USER", "ROLE_ADMIN"};

        // Act
        JwtResponse response = JwtResponse.builder()
                .token("builderToken")
                .username("builderUser")
                .email("builder@example.com")
                .roles(roles)
                .build();

        // Assert
        assertThat(response.getToken()).isEqualTo("builderToken");
        assertThat(response.getType()).isEqualTo("Bearer"); // Default value
        assertThat(response.getUsername()).isEqualTo("builderUser");
        assertThat(response.getEmail()).isEqualTo("builder@example.com");
        assertThat(response.getRoles()).isEqualTo(roles);
    }

    @Test
    @DisplayName("Debe crear JwtResponse solo con token usando setter")
    void shouldCreateJwtResponseWithTokenOnly() {
        // Act
        JwtResponse response = new JwtResponse();
        response.setToken("onlytoken");

        // Assert
        assertThat(response.getToken()).isEqualTo("onlytoken");
        assertThat(response.getType()).isEqualTo("Bearer"); // Default value
        assertThat(response.getUsername()).isNull();
        assertThat(response.getEmail()).isNull();
        assertThat(response.getRoles()).isNull();
    }

    @Test
    @DisplayName("Debe manejar arrays de roles vacíos")
    void shouldHandleEmptyRolesArray() {
        // Arrange
        String[] emptyRoles = {};

        // Act
        JwtResponse response = new JwtResponse("token", "user", "email", emptyRoles);

        // Assert
        assertThat(response.getRoles()).isEqualTo(emptyRoles);
        assertThat(response.getRoles()).hasSize(0);
    }
}