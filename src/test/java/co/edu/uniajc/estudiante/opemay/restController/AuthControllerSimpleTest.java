package co.edu.uniajc.estudiante.opemay.restController;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uniajc.estudiante.opemay.Service.JwtService;
import co.edu.uniajc.estudiante.opemay.Service.UserService;
import co.edu.uniajc.estudiante.opemay.config.TestFirebaseConfig;
import co.edu.uniajc.estudiante.opemay.config.TestSecurityConfig;
import co.edu.uniajc.estudiante.opemay.dto.LoginRequest;
import co.edu.uniajc.estudiante.opemay.model.User;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@Import({TestFirebaseConfig.class, TestSecurityConfig.class})
@SuppressWarnings("deprecation") // Suppress MockBean deprecation warnings until migration
class AuthControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean  
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        loginRequest.setEmail("testuser@example.com");
    }

    @Test
    @WithMockUser
    void testLogin_Success() throws Exception {
        // Arrange
        when(userService.getUserByEmail("testuser@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateTokenFromUsername("testuser")).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"));

        verify(userService).getUserByEmail("testuser@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateTokenFromUsername("testuser");
    }

    @Test
    @WithMockUser
    void testLogin_UserNotFound() throws Exception {
        // Arrange
        when(userService.getUserByEmail("testuser@example.com")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));

        verify(userService).getUserByEmail("testuser@example.com");
    }

    @Test
    @WithMockUser
    void testLogin_InvalidPassword() throws Exception {
        // Arrange
        when(userService.getUserByEmail("testuser@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Contraseña incorrecta"));

        verify(userService).getUserByEmail("testuser@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    @WithMockUser
    void testRefreshToken_Success() throws Exception {
        // Arrange
        String token = "valid-jwt-token";
        when(jwtService.validateToken(anyString())).thenReturn(true);
        when(jwtService.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(jwtService.generateTokenFromUsername(anyString())).thenReturn("new-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void testGetCurrentUser_Success() throws Exception {
        // Arrange
        String token = "valid-jwt-token";
        when(jwtService.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(userService.getUserByUsername(anyString())).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.enabled").value(true));
    }
}