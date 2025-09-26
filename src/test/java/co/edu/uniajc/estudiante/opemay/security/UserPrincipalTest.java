package co.edu.uniajc.estudiante.opemay.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import co.edu.uniajc.estudiante.opemay.model.User;

class UserPrincipalTest {

    private User testUser;
    private UserPrincipal userPrincipal;

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
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(Arrays.asList("USER", "ADMIN"))
                .build();
        
        userPrincipal = UserPrincipal.create(testUser);
    }

    @Test
    void testConstructor() {
        assertNotNull(userPrincipal);
        assertEquals("user-123", userPrincipal.getId());
        assertEquals("testuser", userPrincipal.getUsername());
        assertEquals("test@example.com", userPrincipal.getEmail());
        assertEquals("encodedPassword", userPrincipal.getPassword());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        
        List<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        assertTrue(authorityNames.contains("ROLE_USER"));
        assertTrue(authorityNames.contains("ROLE_ADMIN"));
    }

    @Test
    void testGetAuthoritiesWithNullRoles() {
        User userWithNullRoles = User.builder()
                .id("user-456")
                .username("nullrolesuser")
                .roles(null)
                .build();
        
        // Crear el UserPrincipal y verificar que maneja roles null correctamente
        assertDoesNotThrow(() -> {
            UserPrincipal principalWithNullRoles = UserPrincipal.create(userWithNullRoles);
            Collection<? extends GrantedAuthority> authorities = principalWithNullRoles.getAuthorities();
            
            assertNotNull(authorities);
            assertTrue(authorities.isEmpty());
        });
    }

    @Test
    void testGetAuthoritiesWithEmptyRoles() {
        User userWithEmptyRoles = User.builder()
                .id("user-789")
                .username("emptyrolesuser")
                .roles(List.of())
                .build();
        
        UserPrincipal principalWithEmptyRoles = UserPrincipal.create(userWithEmptyRoles);
        Collection<? extends GrantedAuthority> authorities = principalWithEmptyRoles.getAuthorities();
        
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(userPrincipal.isAccountNonExpired());
        
        // Test con cuenta expirada - crear nuevo principal
        User expiredUser = User.builder()
                .id("user-456")
                .username("expireduser")
                .accountNonExpired(false)
                .build();
        UserPrincipal expiredPrincipal = UserPrincipal.create(expiredUser);
        assertFalse(expiredPrincipal.isAccountNonExpired());
        
        // Test con null - crear nuevo principal
        User nullExpiredUser = User.builder()
                .id("user-789")
                .username("nulluser")
                .accountNonExpired(null)
                .build();
        UserPrincipal nullPrincipal = UserPrincipal.create(nullExpiredUser);
        assertFalse(nullPrincipal.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(userPrincipal.isAccountNonLocked());
        
        // Test con cuenta bloqueada - crear nuevo UserPrincipal
        User lockedUser = User.builder()
                .id("user-456")
                .username("lockeduser")
                .accountNonLocked(false)
                .build();
        UserPrincipal lockedPrincipal = UserPrincipal.create(lockedUser);
        assertFalse(lockedPrincipal.isAccountNonLocked());
        
        // Test con null - crear nuevo UserPrincipal
        User nullUser = User.builder()
                .id("user-789")
                .username("nulluser")
                .accountNonLocked(null)
                .build();
        UserPrincipal nullPrincipal = UserPrincipal.create(nullUser);
        assertFalse(nullPrincipal.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
        
        // Test con credenciales expiradas - crear nuevo UserPrincipal
        User expiredCredsUser = User.builder()
                .id("user-456")
                .username("expiredcredsuser")
                .credentialsNonExpired(false)
                .build();
        UserPrincipal expiredCredsPrincipal = UserPrincipal.create(expiredCredsUser);
        assertFalse(expiredCredsPrincipal.isCredentialsNonExpired());
        
        // Test con null - crear nuevo UserPrincipal
        User nullUser = User.builder()
                .id("user-789")
                .username("nulluser")
                .credentialsNonExpired(null)
                .build();
        UserPrincipal nullPrincipal = UserPrincipal.create(nullUser);
        assertFalse(nullPrincipal.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(userPrincipal.isEnabled());
        
        // Test con usuario deshabilitado - crear nuevo UserPrincipal
        User disabledUser = User.builder()
                .id("user-456")
                .username("disableduser")
                .enabled(false)
                .build();
        UserPrincipal disabledPrincipal = UserPrincipal.create(disabledUser);
        assertFalse(disabledPrincipal.isEnabled());
        
        // Test con null - crear nuevo UserPrincipal
        User nullUser = User.builder()
                .id("user-789")
                .username("nulluser")
                .enabled(null)
                .build();
        UserPrincipal nullPrincipal = UserPrincipal.create(nullUser);
        assertFalse(nullPrincipal.isEnabled());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", userPrincipal.getUsername());
        
        // Test con username null - crear nuevo UserPrincipal
        User nullUsernameUser = User.builder()
                .id("user-456")
                .username(null)
                .build();
        UserPrincipal nullUsernamePrincipal = UserPrincipal.create(nullUsernameUser);
        assertNull(nullUsernamePrincipal.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("encodedPassword", userPrincipal.getPassword());
        
        // Test con password null - crear nuevo UserPrincipal
        User nullPasswordUser = User.builder()
                .id("user-456")
                .username("testuser")
                .password(null)
                .build();
        UserPrincipal nullPasswordPrincipal = UserPrincipal.create(nullPasswordUser);
        assertNull(nullPasswordPrincipal.getPassword());
    }

    @Test
    void testGetId() {
        assertEquals("user-123", userPrincipal.getId());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", userPrincipal.getEmail());
    }

    @Test
    void testWithMinimalUser() {
        // Crear usuario completamente nuevo para evitar cualquier interferencia
        User minimalUser = User.builder()
                .id("minimal-user")
                .username("minimal")
                .build();
        
        // Verificar que el usuario tiene valores null para los campos que esperamos
        assertNull(minimalUser.getEmail());
        assertNull(minimalUser.getPassword());
        assertNull(minimalUser.getRoles());
        assertNull(minimalUser.getEnabled());
        assertNull(minimalUser.getAccountNonExpired());
        assertNull(minimalUser.getAccountNonLocked());
        assertNull(minimalUser.getCredentialsNonExpired());
        
        UserPrincipal minimalPrincipal = UserPrincipal.create(minimalUser);
        
        // Verificar campos básicos
        assertEquals("minimal-user", minimalPrincipal.getId());
        assertEquals("minimal", minimalPrincipal.getUsername());
        assertNull(minimalPrincipal.getEmail());
        assertNull(minimalPrincipal.getPassword());
        
        // Verificar que los valores por defecto son false para campos Boolean null
        assertFalse(minimalPrincipal.isEnabled(), "isEnabled() debe ser false para usuario con enabled=null");
        assertFalse(minimalPrincipal.isAccountNonExpired(), "isAccountNonExpired() debe ser false para usuario con accountNonExpired=null");
        assertFalse(minimalPrincipal.isAccountNonLocked(), "isAccountNonLocked() debe ser false para usuario con accountNonLocked=null");
        assertFalse(minimalPrincipal.isCredentialsNonExpired(), "isCredentialsNonExpired() debe ser false para usuario con credentialsNonExpired=null");
        
        // Verificar authorities
        Collection<? extends GrantedAuthority> authorities = minimalPrincipal.getAuthorities();
        assertNotNull(authorities, "getAuthorities() no debe devolver null");
        assertTrue(authorities.isEmpty(), "getAuthorities() debe devolver una colección vacía para usuario sin roles");
    }

    @Test
    void testEquality() {
        UserPrincipal anotherPrincipal = UserPrincipal.create(testUser);
        
        // Should be equal if they wrap the same user
        assertEquals(userPrincipal.getId(), anotherPrincipal.getId());
        assertEquals(userPrincipal.getUsername(), anotherPrincipal.getUsername());
    }

    @Test
    void testDebugMinimalUserAuthorities() {
        // Test de depuración específico para el problema de authorities
        User debugUser = User.builder()
                .id("debug-user")
                .username("debug")
                .roles(null) // Explícitamente null
                .build();
        
        UserPrincipal debugPrincipal = UserPrincipal.create(debugUser);
        
        Collection<? extends GrantedAuthority> authorities = debugPrincipal.getAuthorities();
        
        // Debug información
        System.out.println("Authorities: " + authorities);
        System.out.println("Authorities class: " + (authorities != null ? authorities.getClass().getName() : "null"));
        System.out.println("Is empty: " + (authorities != null ? authorities.isEmpty() : "N/A"));
        
        assertNotNull(authorities, "authorities no debe ser null");
        assertEquals(0, authorities.size(), "authorities debe tener size 0");
        assertTrue(authorities.isEmpty(), "authorities.isEmpty() debe ser true");
    }

    @Test
    void testToString() {
        String toString = userPrincipal.toString();
        assertNotNull(toString);
        // Verificar que toString no es vacío
        assertFalse(toString.isEmpty());
        // No verificar contenido específico ya que puede variar según la implementación
    }
}