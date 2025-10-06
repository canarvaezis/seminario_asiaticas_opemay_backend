package co.edu.uniajc.estudiante.opemay.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.IRespository.UserRepository;
import co.edu.uniajc.estudiante.opemay.model.User;
import lombok.extern.slf4j.Slf4j;
    
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método simple para JWT filter que no depende de Spring Security UserDetails
    public User loadUserByUsername(String username) {
        try {
            User user = userRepository.getUserByUsername(username);
            if (user == null) {
                log.error("Usuario no encontrado: {}", username);
                return null;
            }
            return user;
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al buscar usuario: {}", e.getMessage());
            return null;
        }
    }

    public User createUser(User user) {
        try {
            // Verificar si el usuario ya existe
            if (userRepository.getUserByUsername(user.getUsername()) != null) {
                throw new RuntimeException("Usuario ya existe: " + user.getUsername());
            }
            
            if (userRepository.getUserByEmail(user.getEmail()) != null) {
                throw new RuntimeException("Email ya registrado: " + user.getEmail());
            }

            // Encriptar contraseña
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Generar ID si no existe
            if (user.getId() == null) {
                user.setId(java.util.UUID.randomUUID().toString());
            }
            
            // Establecer timestamps
            user.setCreatedAt(Timestamp.now());
            user.setUpdatedAt(Timestamp.now());

            userRepository.save(user);
            log.info("Usuario creado exitosamente: {}", user.getUsername());
            return user;
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error creando usuario: {}", e.getMessage());
            throw new RuntimeException("Error al crear usuario", e);
        }
    }

    public User getUserByUsername(String username) {
        try {
            return userRepository.getUserByUsername(username);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error obteniendo usuario por username: {}", e.getMessage());
            return null;
        }
    }

    public User getUserByEmail(String email) {
        try {
            log.info("Buscando usuario por email: '{}'", email);
            User user = userRepository.getUserByEmail(email);
            if (user != null) {
                log.info("Usuario encontrado: ID={}, Email={}", user.getId(), user.getEmail());
            } else {
                log.warn("No se encontró usuario con email: '{}'", email);
            }
            return user;
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error obteniendo usuario por email '{}': {}", email, e.getMessage(), e);
            return null;
        }
    }

    public User getUserById(String id) {
        try {
            return userRepository.getUserById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error obteniendo usuario por ID: {}", e.getMessage());
            return null;
        }
    }

    public List<User> getAllUsers() {
        try {
            return userRepository.getAllUsers();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error obteniendo todos los usuarios: {}", e.getMessage());
            return List.of();
        }
    }

    public User updateUser(User user) {
        try {
            user.setUpdatedAt(Timestamp.now());
            userRepository.update(user);
            return user;
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error actualizando usuario: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    public void deleteUser(String id) {
        try {
            userRepository.delete(id);
            log.info("Usuario eliminado: {}", id);
        } catch (Exception e) {
            log.error("Error eliminando usuario: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    public void updateLastLogin(String username) {
        try {
            User user = getUserByUsername(username);
            if (user != null) {
                user.setLastLogin(Timestamp.now());
                updateUser(user);
            }
        } catch (Exception e) {
            log.error("Error actualizando último login: {}", e.getMessage());
        }
    }  

    // Métodos compatibles con tests (usando Optional)
    public Optional<User> findByUsername(String username) {
        try {
            User user = userRepository.getUserByUsername(username);
            return Optional.ofNullable(user);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error buscando usuario por username: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        try {
            User user = userRepository.getUserByEmail(email);
            return Optional.ofNullable(user);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error buscando usuario por email: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public User save(User user) {
        try {
            // Si no tiene ID, crear nuevo usuario
            if (user.getId() == null || user.getId().isEmpty()) {
                // Generar ID
                user.setId(java.util.UUID.randomUUID().toString());
                
                // Encriptar contraseña si no está encriptada
                if (user.getPassword() != null && !user.getPassword().startsWith("$")) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                
                // Establecer timestamps
                user.setCreatedAt(Timestamp.now());
                user.setUpdatedAt(Timestamp.now());
            } else {
                // Actualizar usuario existente
                user.setUpdatedAt(Timestamp.now());
            }

            userRepository.save(user);
            return user;
            
        } catch (ExecutionException e) {
            log.error("Error guardando usuario: {}", e.getMessage());
            throw new RuntimeException("Error al guardar usuario", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error guardando usuario (interrumpido): {}", e.getMessage());
            throw new RuntimeException("Error al guardar usuario", e);
        }
    }
    //esto 
}
