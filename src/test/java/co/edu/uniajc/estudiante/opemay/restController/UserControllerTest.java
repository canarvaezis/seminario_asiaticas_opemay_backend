package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uniajc.estudiante.opemay.Service.UserService;
import co.edu.uniajc.estudiante.opemay.model.User;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Map<String, String> userData;
    private List<User> testUsers;

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

        userData = new HashMap<>();
        userData.put("username", "newuser");
        userData.put("email", "newuser@example.com");
        userData.put("password", "newPassword123");
        userData.put("firstName", "New");
        userData.put("lastName", "User");

        User user2 = User.builder()
                .id("user-456")
                .username("anotheruser")
                .email("another@example.com")
                .firstName("Another")
                .lastName("User")
                .enabled(true)
                .build();

        testUsers = Arrays.asList(testUser, user2);
    }

    @Test
    @WithMockUser
    void testCreateUser_Success() throws Exception {
        // Arrange
        when(userService.getUserByUsername("newuser")).thenReturn(null);
        when(userService.getUserByEmail("newuser@example.com")).thenReturn(null);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.user").exists());

        verify(userService).getUserByUsername("newuser");
        verify(userService).getUserByEmail("newuser@example.com");
        verify(userService).createUser(any(User.class));
    }

    @Test
    @WithMockUser
    void testCreateUser_MissingRequiredFields() throws Exception {
        // Arrange - datos sin username
        userData.remove("username");

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username, password y email son requeridos"));

        verify(userService, never()).getUserByUsername(any());
        verify(userService, never()).getUserByEmail(any());
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @WithMockUser
    void testCreateUser_UsernameExists() throws Exception {
        // Arrange
        when(userService.getUserByUsername("newuser")).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nombre de usuario ya existe"));

        verify(userService).getUserByUsername("newuser");
        verify(userService, never()).getUserByEmail(any());
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @WithMockUser
    void testCreateUser_EmailExists() throws Exception {
        // Arrange
        when(userService.getUserByUsername("newuser")).thenReturn(null);
        when(userService.getUserByEmail("newuser@example.com")).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El email ya está registrado"));

        verify(userService).getUserByUsername("newuser");
        verify(userService).getUserByEmail("newuser@example.com");
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @WithMockUser
    void testGetAllUsers_Success() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(testUsers);

        // Act & Assert
        mockMvc.perform(get("/api/users/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("user-123"))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].id").value("user-456"))
                .andExpect(jsonPath("$[1].username").value("anotheruser"));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser
    void testGetUserById_Success() throws Exception {
        // Arrange
        when(userService.getUserById("user-123")).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(get("/api/users/user-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-123"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getUserById("user-123");
    }

    @Test
    @WithMockUser
    void testGetUserById_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById("nonexistent")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/users/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));

        verify(userService).getUserById("nonexistent");
    }

    @Test
    @WithMockUser
    void testUpdateUser_Success() throws Exception {
        // Arrange
        when(userService.getUserById("user-123")).thenReturn(testUser);
        when(userService.updateUser(any(User.class))).thenReturn(testUser);

        Map<String, String> updateData = new HashMap<>();
        updateData.put("firstName", "Updated");
        updateData.put("lastName", "Name");

        // Act & Assert
        mockMvc.perform(put("/api/users/user-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.user").exists());

        verify(userService).getUserById("user-123");
        verify(userService).updateUser(any(User.class));
    }

    @Test
    @WithMockUser
    void testUpdateUser_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById("nonexistent")).thenReturn(null);

        Map<String, String> updateData = new HashMap<>();
        updateData.put("firstName", "Updated");

        // Act & Assert
        mockMvc.perform(put("/api/users/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));

        verify(userService).getUserById("nonexistent");
        verify(userService, never()).updateUser(any(User.class));
    }

    @Test
    @WithMockUser
    void testDeleteUser_Success() throws Exception {
        // Arrange
        when(userService.getUserById("user-123")).thenReturn(testUser);
        doNothing().when(userService).deleteUser("user-123");

        // Act & Assert
        mockMvc.perform(delete("/api/users/user-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario eliminado correctamente"));

        verify(userService).getUserById("user-123");
        verify(userService).deleteUser("user-123");
    }

    @Test
    @WithMockUser
    void testDeleteUser_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById("nonexistent")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(delete("/api/users/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));

        verify(userService).getUserById("nonexistent");
        verify(userService, never()).deleteUser(any());
    }

    @Test
    @WithMockUser
    void testServiceException_GetAll() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser
    void testServiceException_Create() throws Exception {
        // Arrange
        when(userService.getUserByUsername("newuser")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testControllerExists() {
        UserController controller = new UserController(userService);
        assertNotNull(controller);
    }

    @Test
    @WithMockUser
    void testCreateUser_MissingEmail() throws Exception {
        // Arrange - datos sin email
        userData.remove("email");

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username, password y email son requeridos"));
    }

    @Test
    @WithMockUser
    void testCreateUser_MissingPassword() throws Exception {
        // Arrange - datos sin password
        userData.remove("password");

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username, password y email son requeridos"));
    }

    @Test
    @WithMockUser
    void testGetAllUsers_EmptyList() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}