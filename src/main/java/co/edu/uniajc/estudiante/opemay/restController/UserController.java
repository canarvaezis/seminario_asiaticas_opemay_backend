package co.edu.uniajc.estudiante.opemay.restController;

import java.util.Arrays;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.Service.UserService;
import co.edu.uniajc.estudiante.opemay.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    // 🔹 Constantes para evitar duplicar literales
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> userData) {
        try {
            log.info("Creando nuevo usuario: {}", userData.get(USERNAME));

            // Validar datos requeridos
            if (userData.get(USERNAME) == null || userData.get(PASSWORD) == null
                    || userData.get(EMAIL) == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(ERROR, "Username, password y email son requeridos"));
            }

            // Verificar si el usuario ya existe
            if (userService.getUserByUsername(userData.get(USERNAME)) != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(ERROR, "El nombre de usuario ya existe"));
            }
            if (userService.getUserByEmail(userData.get(EMAIL)) != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(ERROR, "El email ya está registrado"));
            }

            // Crear nuevo usuario
            User newUser = User.builder()
                    .username(userData.get(USERNAME))
                    .password(userData.get(PASSWORD))
                    .email(userData.get(EMAIL))
                    .firstName(userData.getOrDefault(FIRST_NAME, ""))
                    .lastName(userData.getOrDefault(LAST_NAME, ""))
                    .roles(Arrays.asList("USER"))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .createdAt(Timestamp.now())
                    .updatedAt(Timestamp.now())
                    .build();

            User createdUser = userService.createUser(newUser);
            log.info("Usuario creado exitosamente: {}", createdUser.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            MESSAGE, "Usuario creado exitosamente",
                            "user", Map.of(
                                    USERNAME, createdUser.getUsername(),
                                    EMAIL, createdUser.getEmail(),
                                    "id", createdUser.getId()
                            )
                    ));

        } catch (Exception e) {
            log.error("Error creando usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR, "Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(ERROR, "Usuario no encontrado"));
            }

            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    USERNAME, user.getUsername(),
                    EMAIL, user.getEmail(),
                    FIRST_NAME, user.getFirstName(),
                    LAST_NAME, user.getLastName(),
                    "roles", user.getRoles(),
                    "enabled", user.getEnabled(),
                    "accountNonExpired", user.getAccountNonExpired(),
                    "createdAt", user.getCreatedAt(),
                    "lastLogin", user.getLastLogin()
            ));

        } catch (Exception e) {
            log.error("Error obteniendo usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR, "Error interno del servidor"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            var users = userService.getAllUsers();

            var filteredUsers = users.stream()
                    .map(user -> Map.of(
                            "id", user.getId(),
                            USERNAME, user.getUsername(),
                            EMAIL, user.getEmail(),
                            FIRST_NAME, user.getFirstName(),
                            LAST_NAME, user.getLastName(),
                            "enabled", user.getEnabled(),
                            "createdAt", user.getCreatedAt()
                    ))
                    .toList();

            return ResponseEntity.ok(filteredUsers);

        } catch (Exception e) {
            log.error("Error obteniendo usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR, "Error interno del servidor"));
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username,
                                        @RequestBody Map<String, String> userData) {
        try {
            User existingUser = userService.getUserByUsername(username);

            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(ERROR, "Usuario no encontrado"));
            }

            if (userData.containsKey(FIRST_NAME)) {
                existingUser.setFirstName(userData.get(FIRST_NAME));
            }
            if (userData.containsKey(LAST_NAME)) {
                existingUser.setLastName(userData.get(LAST_NAME));
            }
            if (userData.containsKey(EMAIL)) {
                existingUser.setEmail(userData.get(EMAIL));
            }

            existingUser.setUpdatedAt(Timestamp.now());

            User updatedUser = userService.updateUser(existingUser);

            return ResponseEntity.ok(Map.of(
                    MESSAGE, "Usuario actualizado exitosamente",
                    "user", Map.of(
                            USERNAME, updatedUser.getUsername(),
                            EMAIL, updatedUser.getEmail(),
                            FIRST_NAME, updatedUser.getFirstName(),
                            LAST_NAME, updatedUser.getLastName(),
                            "id", updatedUser.getId()
                    )
            ));

        } catch (Exception e) {
            log.error("Error actualizando usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR, "Error interno del servidor"));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(ERROR, "Usuario no encontrado"));
            }

            userService.deleteUser(user.getId());

            return ResponseEntity.ok(Map.of(
                    MESSAGE, "Usuario eliminado correctamente",
                    USERNAME, username
            ));

        } catch (Exception e) {
            log.error("Error eliminando usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR, "Error interno del servidor"));
        }
    }
}
