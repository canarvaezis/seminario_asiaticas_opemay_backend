package co.edu.uniajc.estudiante.opemay.restController;
import com.google.cloud.Timestamp;
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
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> userData) {
        try {
            log.info("Creando nuevo usuario: {}", userData.get("username"));
            
            // Validar datos requeridos
            if (userData.get("username") == null || userData.get("password") == null 
                || userData.get("email") == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username, password y email son requeridos"));
            }
            // Verificar si el usuario ya existe
            if (userService.getUserByUsername(userData.get("username")) != null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre de usuario ya existe"));
            }
            if (userService.getUserByEmail(userData.get("email")) != null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email ya está registrado"));
            }
            // Crear nuevo usuario
            User newUser = User.builder()
                .username(userData.get("username"))
                .password(userData.get("password"))
                .email(userData.get("email"))
                .firstName(userData.getOrDefault("firstName", ""))
                .lastName(userData.getOrDefault("lastName", ""))
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
                    "message", "Usuario creado exitosamente",
                    "username", createdUser.getUsername(),
                    "email", createdUser.getEmail(),
                    "id", createdUser.getId()
                ));
                
        } catch (Exception e) {
            log.error("Error creando usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            // No devolver la contraseña en la respuesta
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "roles", user.getRoles(),
                "enabled", user.getEnabled(),
                "accountNonExpired", user.getAccountNonExpired(),
                "createdAt", user.getCreatedAt(),
                "lastLogin", user.getLastLogin()
            ));
            
        } catch (Exception e) {
            log.error("Error obteniendo usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            var users = userService.getAllUsers();
            
            // Filtrar datos sensibles
            var filteredUsers = users.stream()
                .map(user -> {
                    return Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "enabled", user.getEnabled(),
                        "createdAt", user.getCreatedAt()
                    );
                })
                .toList();
            
            return ResponseEntity.ok(filteredUsers);
            
        } catch (Exception e) {
            log.error("Error obteniendo usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, 
                                       @RequestBody Map<String, String> userData) {
        try {
            User existingUser = userService.getUserByUsername(username);
            
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Actualizar campos permitidos
            if (userData.containsKey("firstName")) {
                existingUser.setFirstName(userData.get("firstName"));
            }
            if (userData.containsKey("lastName")) {
                existingUser.setLastName(userData.get("lastName"));
            }
            if (userData.containsKey("email")) {
                existingUser.setEmail(userData.get("email"));
            }
            
            existingUser.setUpdatedAt(Timestamp.now());
            
            User updatedUser = userService.updateUser(existingUser);
            
            return ResponseEntity.ok(Map.of(
                "message", "Usuario actualizado exitosamente",
                "username", updatedUser.getUsername()
            ));
            
        } catch (Exception e) {
            log.error("Error actualizando usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            userService.deleteUser(user.getId());
            
            return ResponseEntity.ok(Map.of(
                "message", "Usuario eliminado exitosamente",
                "username", username
            ));
            
        } catch (Exception e) {
            log.error("Error eliminando usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
}
