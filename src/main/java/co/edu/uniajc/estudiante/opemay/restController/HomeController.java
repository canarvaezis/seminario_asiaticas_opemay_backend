package co.edu.uniajc.estudiante.opemay.restController;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
            "message", "¡Bienvenido a Opemay API!",
            "description", "API de comercio electrónico con autenticación JWT",
            "version", "1.0.0",
            "endpoints", Map.of(
                "auth", Map.of(
                    "register", "POST /api/auth/register",
                    "login", "POST /api/auth/login"
                ),
                "products", Map.of(
                    "getAll", "GET /api/products/all",
                    "getById", "GET /api/products/{id}",
                    "create", "POST /api/products/save"
                ),
                "users", Map.of(
                    "getAll", "GET /api/users/all (requiere autenticación)",
                    "getById", "GET /api/users/{id} (requiere autenticación)",
                    "update", "PUT /api/users/{id} (requiere autenticación)",
                    "delete", "DELETE /api/users/{id} (requiere autenticación)"
                )
            ),
            "status", "API funcionando correctamente"
        ));
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
