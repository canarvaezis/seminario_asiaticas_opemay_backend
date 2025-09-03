package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.dto.LoginRequest;
import co.edu.uniajc.estudiante.opemay.dto.RegisterRequest;
import co.edu.uniajc.estudiante.opemay.Service.JwtService;
import co.edu.uniajc.estudiante.opemay.Service.UserService;
import co.edu.uniajc.estudiante.opemay.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Intento de login para usuario: {}", loginRequest.getUsername());
            
            // Buscar usuario
            User user = userService.getUserByUsername(loginRequest.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado"));
            }
            
            // Verificar contraseña
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Contraseña incorrecta"));
            }
            
            // Generar token JWT
            String jwt = jwtService.generateTokenFromUsername(user.getUsername());
            
            log.info("Login exitoso para usuario: {}", loginRequest.getUsername());
            
            return ResponseEntity.ok(Map.of(
                "token", jwt,
                "type", "Bearer",
                "username", user.getUsername()
            ));
            
        } catch (Exception e) {
            log.error("Error de autenticación para usuario: {} - {}", 
                loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Error de autenticación"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Intento de registro para usuario: {}", registerRequest.getUsername());
            
            // Verificar si el usuario ya existe
            if (userService.getUserByUsername(registerRequest.getUsername()) != null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre de usuario ya existe"));
            }

            if (userService.getUserByEmail(registerRequest.getEmail()) != null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email ya está registrado"));
            }

            // Crear usuario basado en RegisterRequest
            User newUser = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .roles(java.util.Arrays.asList("USER"))
                .enabled(true)
                .build();

            User createdUser = userService.createUser(newUser);
            
            log.info("Usuario registrado exitosamente: {}", registerRequest.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "Usuario registrado exitosamente",
                    "username", createdUser.getUsername()
                ));
                
        } catch (Exception e) {
            log.error("Error durante el registro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token inválido"));
            }

            String token = authHeader.substring(7);
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token expirado o inválido"));
            }

            String username = jwtService.getUsernameFromToken(token);
            String newToken = jwtService.generateTokenFromUsername(username);
            
            return ResponseEntity.ok(Map.of(
                "token", newToken,
                "type", "Bearer",
                "username", username
            ));
            
        } catch (Exception e) {
            log.error("Error al refrescar token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "No se pudo refrescar el token"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token requerido"));
            }

            String token = authHeader.substring(7);
            String username = jwtService.getUsernameFromToken(token);
            
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "roles", user.getRoles(),
                "enabled", user.getEnabled()
            ));
            
        } catch (Exception e) {
            log.error("Error al obtener usuario actual: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Token inválido"));
        }
    }
}
