package co.edu.uniajc.estudiante.opemay.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Tests unitarios para RegisterRequest DTO
 */
@DisplayName("RegisterRequest DTO Tests")
class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe crear RegisterRequest válido con todos los campos")
    void shouldCreateValidRegisterRequest() {
        // Act
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // Assert
        assertThat(request).isNotNull();
        assertThat(request.getUsername()).isEqualTo("testuser");
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Debe fallar validación con username vacío")
    void shouldFailValidationWithEmptyUsername() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername(""); // Empty username
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .anyMatch(message -> message.contains("username") && message.contains("vacío"));
    }

    @Test
    @DisplayName("Debe fallar validación con email inválido")
    void shouldFailValidationWithInvalidEmail() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("invalid-email"); // Invalid email format
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .anyMatch(message -> message.contains("email") || message.contains("valid"));
    }

    @Test
    @DisplayName("Debe fallar validación con password vacío")
    void shouldFailValidationWithEmptyPassword() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword(""); // Empty password

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .anyMatch(message -> message.contains("password") && message.contains("vacío"));
    }

    @Test
    @DisplayName("Debe manejar valores null")
    void shouldHandleNullValues() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        // All fields are null by default

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty(); // Should have validation errors for required fields
        assertThat(request.getUsername()).isNull();
        assertThat(request.getEmail()).isNull();
        assertThat(request.getPassword()).isNull();
    }

    @Test
    @DisplayName("Equals y HashCode deben funcionar correctamente")
    void equalsAndHashCodeShouldWorkCorrectly() {
        // Arrange
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("user");
        request1.setEmail("email@test.com");
        request1.setPassword("pass");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("user");
        request2.setEmail("email@test.com");
        request2.setPassword("pass");

        RegisterRequest request3 = new RegisterRequest();
        request3.setUsername("different");
        request3.setEmail("email@test.com");
        request3.setPassword("pass");

        // Assert
        assertThat(request1).isEqualTo(request2);
        assertThat(request1).isNotEqualTo(request3);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("ToString debe contener información relevante sin password")
    void toStringShouldContainRelevantInfoWithoutPassword() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("secretpassword");

        // Act
        String requestString = request.toString();

        // Assert
        assertThat(requestString).contains("testuser");
        assertThat(requestString).contains("test@example.com");
        // Password should not be in toString for security
        assertThat(requestString).doesNotContain("secretpassword");
    }

    @Test
    @DisplayName("Debe validar email con formato correcto")
    void shouldValidateEmailWithCorrectFormat() {
        // Test cases for valid emails
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.org",
            "user+tag@example.co.uk",
            "123@numbers.com"
        };

        for (String email : validEmails) {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setUsername("testuser");
            request.setEmail(email);
            request.setPassword("password123");

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations)
                .as("Email %s should be valid", email)
                .filteredOn(v -> v.getPropertyPath().toString().equals("email"))
                .isEmpty();
        }
    }

    @Test
    @DisplayName("Debe rechazar emails con formato incorrecto")
    void shouldRejectInvalidEmailFormats() {
        // Test cases for invalid emails
        String[] invalidEmails = {
            "plainaddress",
            "@missingtld.com",
            "missing@.com",
            "missing.domain@.com",
            "spaces @example.com"
        };

        for (String email : invalidEmails) {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setUsername("testuser");
            request.setEmail(email);
            request.setPassword("password123");

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations)
                .as("Email %s should be invalid", email)
                .filteredOn(v -> v.getPropertyPath().toString().equals("email"))
                .isNotEmpty();
        }
    }
}