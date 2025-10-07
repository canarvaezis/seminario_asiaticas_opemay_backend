package co.edu.uniajc.estudiante.opemay.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.uniajc.estudiante.opemay.Service.UserService;
import co.edu.uniajc.estudiante.opemay.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Primero intentar buscar por email (que es lo que viene del JWT)
        User user = userService.getUserByEmail(usernameOrEmail);
        
        // Si no se encuentra por email, intentar por username
        if (user == null) {
            user = userService.getUserByUsername(usernameOrEmail);
        }
        
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail);
        }

        // Retornar UserPrincipal en lugar de User estándar
        return UserPrincipal.create(user);
    }
}
