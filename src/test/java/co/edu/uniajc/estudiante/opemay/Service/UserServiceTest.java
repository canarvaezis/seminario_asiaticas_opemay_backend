package co.edu.uniajc.estudiante.opemay.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.IRespository.UserRepository;
import co.edu.uniajc.estudiante.opemay.model.User;

/**
 * Tests unitarios para UserService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("test-id")
                .username("testuser")
                .password("rawPassword")
                .email("test@example.com")
                .enabled(true)
                .createdAt(Timestamp.now())
                .build();
    }

    @Nested
    @DisplayName("createUser Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Debe crear un usuario exitosamente")
        void shouldCreateUserSuccessfully() throws Exception {
            // Arrange
            when(userRepository.getUserByUsername("testuser")).thenReturn(null); // No existe previamente
            when(userRepository.getUserByEmail("test@example.com")).thenReturn(null); // Email no usado
            when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

            // Act
            User result = userService.createUser(testUser);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(passwordEncoder).encode("rawPassword");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Debe generar ID automáticamente si no existe")
        void shouldGenerateIdWhenNotProvided() throws Exception {
            // Arrange
            User userWithoutId = User.builder()
                    .username("testuser")
                    .password("password")
                    .email("test@example.com")
                    .build();
            
            when(userRepository.getUserByUsername("testuser")).thenReturn(null);
            when(userRepository.getUserByEmail("test@example.com")).thenReturn(null);
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

            // Act
            User result = userService.createUser(userWithoutId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando usuario ya existe")
        void shouldThrowExceptionWhenUserAlreadyExists() throws Exception {
            // Arrange
            when(userRepository.getUserByUsername("testuser")).thenReturn(testUser);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.createUser(testUser));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando email ya existe")
        void shouldThrowExceptionWhenEmailAlreadyExists() throws Exception {
            // Arrange
            when(userRepository.getUserByUsername("testuser")).thenReturn(null);
            when(userRepository.getUserByEmail("test@example.com")).thenReturn(testUser);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.createUser(testUser));
        }

        @Test
        @DisplayName("Debe manejar excepción durante creación")
        void shouldHandleExceptionDuringCreation() throws Exception {
            // Arrange
            when(userRepository.getUserByUsername("testuser")).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.createUser(testUser));
        }
    }

    @Nested
    @DisplayName("loadUserByUsername Tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Debe cargar usuario por username exitosamente")
        void shouldLoadUserByUsernameSuccessfully() throws Exception {
            // Arrange
            String username = "testuser";
            when(userRepository.getUserByUsername(username)).thenReturn(testUser);

            // Act
            User result = userService.loadUserByUsername(username);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(username);
            verify(userRepository).getUserByUsername(username);
        }

        @Test
        @DisplayName("Debe retornar null cuando usuario no existe")
        void shouldReturnNullWhenUserNotExists() throws Exception {
            // Arrange
            String username = "nonexistent";
            when(userRepository.getUserByUsername(username)).thenReturn(null);

            // Act
            User result = userService.loadUserByUsername(username);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Debe retornar null en caso de excepción")
        void shouldReturnNullOnException() throws Exception {
            // Arrange
            String username = "testuser";
            when(userRepository.getUserByUsername(username)).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act
            User result = userService.loadUserByUsername(username);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getUserByUsername Tests")
    class GetUserByUsernameTests {

        @Test
        @DisplayName("Debe obtener usuario por username exitosamente")
        void shouldGetUserByUsernameSuccessfully() throws Exception {
            // Arrange
            String username = "testuser";
            when(userRepository.getUserByUsername(username)).thenReturn(testUser);

            // Act
            User result = userService.getUserByUsername(username);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(username);
            verify(userRepository).getUserByUsername(username);
        }

        @Test
        @DisplayName("Debe retornar null cuando usuario no existe")
        void shouldReturnNullWhenUserByUsernameNotExists() throws Exception {
            // Arrange
            String username = "nonexistent";
            when(userRepository.getUserByUsername(username)).thenReturn(null);

            // Act
            User result = userService.getUserByUsername(username);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Debe retornar null en caso de excepción")
        void shouldReturnNullOnExceptionInGetByUsername() throws Exception {
            // Arrange
            String username = "testuser";
            when(userRepository.getUserByUsername(username)).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act
            User result = userService.getUserByUsername(username);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getUserByEmail Tests")
    class GetUserByEmailTests {

        @Test
        @DisplayName("Debe obtener usuario por email exitosamente")
        void shouldGetUserByEmailSuccessfully() throws Exception {
            // Arrange
            String email = "test@example.com";
            when(userRepository.getUserByEmail(email)).thenReturn(testUser);

            // Act
            User result = userService.getUserByEmail(email);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
            verify(userRepository).getUserByEmail(email);
        }

        @Test
        @DisplayName("Debe retornar null cuando usuario no existe")
        void shouldReturnNullWhenUserByEmailNotExists() throws Exception {
            // Arrange
            String email = "nonexistent@example.com";
            when(userRepository.getUserByEmail(email)).thenReturn(null);

            // Act
            User result = userService.getUserByEmail(email);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Debe retornar null en caso de excepción")
        void shouldReturnNullOnExceptionInGetByEmail() throws Exception {
            // Arrange
            String email = "test@example.com";
            when(userRepository.getUserByEmail(email)).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act
            User result = userService.getUserByEmail(email);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getUserById Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Debe obtener usuario por ID exitosamente")
        void shouldGetUserByIdSuccessfully() throws Exception {
            // Arrange
            String userId = "test-id";
            when(userRepository.getUserById(userId)).thenReturn(testUser);

            // Act
            User result = userService.getUserById(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(userId);
            verify(userRepository).getUserById(userId);
        }

        @Test
        @DisplayName("Debe retornar null cuando usuario no existe")
        void shouldReturnNullWhenUserByIdNotExists() throws Exception {
            // Arrange
            String userId = "non-existent-id";
            when(userRepository.getUserById(userId)).thenReturn(null);

            // Act
            User result = userService.getUserById(userId);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Debe retornar null en caso de excepción")
        void shouldReturnNullOnExceptionInGetById() throws Exception {
            // Arrange
            String userId = "test-id";
            when(userRepository.getUserById(userId)).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act
            User result = userService.getUserById(userId);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getAllUsers Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Debe obtener todos los usuarios exitosamente")
        void shouldGetAllUsersSuccessfully() throws Exception {
            // Arrange
            User user1 = User.builder().id("1").username("user1").email("user1@example.com").build();
            User user2 = User.builder().id("2").username("user2").email("user2@example.com").build();
            List<User> users = Arrays.asList(user1, user2);
            
            when(userRepository.getAllUsers()).thenReturn(users);

            // Act
            List<User> result = userService.getAllUsers();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            verify(userRepository).getAllUsers();
        }

        @Test
        @DisplayName("Debe retornar lista vacía en caso de excepción")
        void shouldReturnEmptyListOnException() throws Exception {
            // Arrange
            when(userRepository.getAllUsers()).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act
            List<User> result = userService.getAllUsers();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateUser Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Debe actualizar usuario exitosamente")
        void shouldUpdateUserSuccessfully() throws Exception {
            // Arrange
            // No necesitamos configurar mock para update ya que no devuelve nada

            // Act
            User result = userService.updateUser(testUser);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
            verify(userRepository).update(any(User.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción en caso de error")
        void shouldThrowExceptionOnUpdateError() throws Exception {
            // Arrange
            when(userRepository.update(any(User.class))).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.updateUser(testUser));
        }
    }

    @Nested
    @DisplayName("deleteUser Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Debe eliminar usuario exitosamente")
        void shouldDeleteUserSuccessfully() throws Exception {
            // Arrange
            String userId = "test-id";

            // Act
            userService.deleteUser(userId);

            // Assert
            verify(userRepository).delete(userId);
        }

        @Test
        @DisplayName("Debe lanzar excepción en caso de error")
        void shouldThrowExceptionOnDeleteError() throws Exception {
            // Arrange
            String userId = "test-id";
            when(userRepository.delete(userId)).thenThrow(new RuntimeException("Error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        }
    }

    @Nested
    @DisplayName("updateLastLogin Tests")
    class UpdateLastLoginTests {

        @Test
        @DisplayName("Debe actualizar último login exitosamente")
        void shouldUpdateLastLoginSuccessfully() throws Exception {
            // Arrange
            String username = "testuser";
            when(userRepository.getUserByUsername(username)).thenReturn(testUser);

            // Act
            userService.updateLastLogin(username);

            // Assert
            verify(userRepository).getUserByUsername(username);
            verify(userRepository).update(any(User.class));
        }

        @Test
        @DisplayName("No debe fallar si usuario no existe")
        void shouldNotFailIfUserNotExists() throws Exception {
            // Arrange
            String username = "nonexistent";
            when(userRepository.getUserByUsername(username)).thenReturn(null);

            // Act & Assert (no debería lanzar excepción)
            userService.updateLastLogin(username);
        }

        @Test
        @DisplayName("Debe manejar excepciones silenciosamente")
        void shouldHandleExceptionsSilently() throws Exception {
            // Arrange
            String username = "testuser";
            when(userRepository.getUserByUsername(username)).thenThrow(new RuntimeException("Error"));

            // Act & Assert (no debería lanzar excepción)
            userService.updateLastLogin(username);
        }
    }
}