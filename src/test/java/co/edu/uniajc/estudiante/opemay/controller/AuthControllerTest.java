package co.edu.uniajc.estudiante.opemay.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import co.edu.uniajc.estudiante.opemay.dto.LoginRequest;
import co.edu.uniajc.estudiante.opemay.dto.RegisterRequest;
import co.edu.uniajc.estudiante.opemay.Service.JwtService;
import co.edu.uniajc.estudiante.opemay.Service.UserService;
import co.edu.uniajc.estudiante.opemay.model.User;
import co.edu.uniajc.estudiante.opemay.restController.AuthController;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests de integración para AuthController
 */
@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("POST /api/auth/login debe autenticar usuario válido")
    void shouldAuthenticateValidUser() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateTokenFromUsername("testuser")).thenReturn("jwt.token.here");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("POST /api/auth/login debe rechazar usuario no encontrado")
    void shouldRejectUserNotFound() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("password");

        when(userService.getUserByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }

    @Test
    @DisplayName("POST /api/auth/login debe rechazar contraseña incorrecta")
    void shouldRejectWrongPassword() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Contraseña incorrecta"));
    }

    @Test
    @DisplayName("POST /api/auth/register debe registrar nuevo usuario")
    void shouldRegisterNewUser() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");

        User savedUser = User.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedPassword")
                .build();

        when(userService.getUserByUsername("newuser")).thenReturn(null);
        when(userService.getUserByEmail("newuser@example.com")).thenReturn(null);
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/auth/register debe rechazar username duplicado")
    void shouldRejectDuplicateUsername() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("existinguser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");

        User existingUser = User.builder()
                .username("existinguser")
                .email("existing@example.com")
                .build();

        when(userService.getUserByUsername("existinguser")).thenReturn(existingUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("El nombre de usuario ya existe"));
    }

    @Test
    @DisplayName("POST /api/auth/register debe rechazar email duplicado")
    void shouldRejectDuplicateEmail() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("existing@example.com");
        registerRequest.setPassword("password123");

        User existingUser = User.builder()
                .username("otheruser")
                .email("existing@example.com")
                .build();

        when(userService.getUserByUsername("newuser")).thenReturn(null);
        when(userService.getUserByEmail("existing@example.com")).thenReturn(existingUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("El email ya está registrado"));
    }

    @Test
    @DisplayName("POST /api/auth/login debe manejar request body inválido")
    void shouldHandleInvalidRequestBody() throws Exception {
        // Arrange - Invalid JSON
        String invalidJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe manejar excepciones internas del servidor")
    void shouldHandleInternalServerErrors() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        when(userService.getUserByUsername("testuser"))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Error de autenticación"));
    }

    @Test
    @DisplayName("GET /api/auth/me debe obtener información del usuario actual")
    void shouldGetCurrentUserInfo() throws Exception {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        when(jwtService.getUsernameFromToken("valid.token")).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer valid.token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("GET /api/auth/me debe rechazar token faltante")
    void shouldRejectMissingToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/me"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Token requerido"));
    }
}