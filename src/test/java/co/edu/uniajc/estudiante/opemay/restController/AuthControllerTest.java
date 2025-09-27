package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

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
import co.edu.uniajc.estudiante.opemay.dto.RegisterRequest;
import co.edu.uniajc.estudiante.opemay.model.User;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@Import({TestFirebaseConfig.class, TestSecurityConfig.class})
@SuppressWarnings("deprecation") // Suppress MockBean deprecation warnings until migration
class AuthControllerTest {

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
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("newPassword123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");
    }

    @Test
    @WithMockUser
    void testLogin_Success() throws Exception {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateToken("testuser");
    }

    @Test
    @WithMockUser
    void testLogin_UserNotFound() throws Exception {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @WithMockUser
    void testLogin_InvalidPassword() throws Exception {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @WithMockUser
    void testLogin_UserNotEnabled() throws Exception {
        // Arrange
        testUser.setEnabled(false);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @WithMockUser
    void testRegister_Success() throws Exception {
        // Arrange
        when(userService.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userService.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userService.save(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).findByUsername("newuser");
        verify(userService).findByEmail("newuser@example.com");
        verify(passwordEncoder).encode("newPassword123");
        verify(userService).save(any(User.class));
    }

    @Test
    @WithMockUser
    void testRegister_UsernameAlreadyExists() throws Exception {
        // Arrange
        when(userService.findByUsername("newuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).findByUsername("newuser");
        verify(userService, never()).findByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userService, never()).save(any(User.class));
    }

    @Test
    @WithMockUser
    void testRegister_EmailAlreadyExists() throws Exception {
        // Arrange
        when(userService.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userService.findByEmail("newuser@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(userService).findByUsername("newuser");
        verify(userService).findByEmail("newuser@example.com");
        verify(passwordEncoder, never()).encode(any());
        verify(userService, never()).save(any(User.class));
    }

    @Test
    @WithMockUser
    void testLogin_WithNullRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testRegister_WithNullRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testLogin_ServiceException() throws Exception {
        // Arrange
        when(userService.findByUsername("testuser")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());

        verify(userService).findByUsername("testuser");
    }

    @Test
    @WithMockUser
    void testRegister_ServiceException() throws Exception {
        // Arrange
        when(userService.findByUsername("newuser")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError());

        verify(userService).findByUsername("newuser");
    }

    @Test
    void testControllerExists() {
        AuthController controller = new AuthController(userService, jwtService, passwordEncoder);
        assertNotNull(controller);
    }

    @Test
    void testLoginRequestValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        
        assertEquals("testuser", request.getUsername());
        assertEquals("password", request.getPassword());
    }

    @Test
    void testRegisterRequestValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setEmail("user@test.com");
        request.setPassword("pass");
        request.setFirstName("First");
        request.setLastName("Last");
        
        assertEquals("user", request.getUsername());
        assertEquals("user@test.com", request.getEmail());
        assertEquals("pass", request.getPassword());
        assertEquals("First", request.getFirstName());
        assertEquals("Last", request.getLastName());
    }
}