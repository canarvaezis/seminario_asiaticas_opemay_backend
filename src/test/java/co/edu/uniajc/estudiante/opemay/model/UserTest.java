package co.edu.uniajc.estudiante.opemay.model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.cloud.Timestamp;

/**
 * Tests unitarios para el modelo User
 */
@DisplayName("User Model Tests")
class UserTest {

    @Test
    @DisplayName("Debe crear usuario con builder")
    void shouldCreateUserWithBuilder() {
        // Act
        User user = User.builder()
                .id("test-id")
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(List.of("USER"))
                .createdAt(Timestamp.now())
                .build();

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("test-id");
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getAccountNonExpired()).isTrue();
        assertThat(user.getAccountNonLocked()).isTrue();
        assertThat(user.getCredentialsNonExpired()).isTrue();
        assertThat(user.getRoles()).containsExactly("USER");
    }

    @Test
    @DisplayName("Debe usar valores por defecto")
    void shouldUseDefaultValues() {
        // Act
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        // Assert
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getAccountNonExpired()).isTrue();
        assertThat(user.getAccountNonLocked()).isTrue();
        assertThat(user.getCredentialsNonExpired()).isTrue();
        assertThat(user.getRoles()).containsExactly("USER");
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe permitir modificar propiedades")
    void shouldAllowModifyingProperties() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        // Act
        user.setFirstName("Updated");
        user.setLastName("Name");
        user.setEnabled(false);
        user.setRoles(List.of("ADMIN", "USER"));

        // Assert
        assertThat(user.getFirstName()).isEqualTo("Updated");
        assertThat(user.getLastName()).isEqualTo("Name");
        assertThat(user.getEnabled()).isFalse();
        assertThat(user.getRoles()).containsExactly("ADMIN", "USER");
    }

    @Test
    @DisplayName("ToString debe excluir password")
    void toStringShouldExcludePassword() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("secretpassword")
                .build();

        // Act
        String userString = user.toString();

        // Assert
        assertThat(userString).doesNotContain("secretpassword");
        assertThat(userString).contains("testuser");
        assertThat(userString).contains("test@example.com");
    }

    @Test
    @DisplayName("Debe permitir timestamps null")
    void shouldAllowNullTimestamps() {
        // Act
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .lastLogin(null)
                .updatedAt(null)
                .build();

        // Assert
        assertThat(user.getLastLogin()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
        assertThat(user.getCreatedAt()).isNotNull(); // Tiene valor por defecto
    }

    @Test
    @DisplayName("Debe manejar roles vacíos")
    void shouldHandleEmptyRoles() {
        // Act
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .roles(List.of())
                .build();

        // Assert
        assertThat(user.getRoles()).isEmpty();
    }

    @Test
    @DisplayName("Equals y HashCode deben funcionar correctamente")
    void equalsAndHashCodeShouldWorkCorrectly() {
        // Arrange
        User user1 = User.builder()
                .id("test-id")
                .username("testuser")
                .email("test@example.com")
                .build();

        User user2 = User.builder()
                .id("test-id")
                .username("testuser")
                .email("test@example.com")
                .build();

        User user3 = User.builder()
                .id("different-id")
                .username("testuser")
                .email("test@example.com")
                .build();

        // Assert
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
    }
}