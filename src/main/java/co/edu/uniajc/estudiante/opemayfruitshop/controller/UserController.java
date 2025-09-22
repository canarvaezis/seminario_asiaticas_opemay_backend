package co.edu.uniajc.estudiante.opemayfruitshop.controller;

import co.edu.uniajc.estudiante.opemayfruitshop.model.User;
import co.edu.uniajc.estudiante.opemayfruitshop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ✅ Crear
    @PostMapping
    public ResponseEntity<User> createUser(@RequestParam String email,
                                           @RequestParam String password,
                                           @RequestParam String name,
                                           @RequestParam String address,
                                           @RequestParam String phone) {
        return ResponseEntity.ok(service.createUser(email, password, name, address, phone));
    }

    // ✅ Leer uno
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = service.getUser(id);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    // ✅ Listar todos
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    // ✅ Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id); // aseguramos que use el UID correcto
        return ResponseEntity.ok(service.updateUser(user));
    }

    // ✅ Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
