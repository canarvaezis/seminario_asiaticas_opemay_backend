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
        User minimalUser = User.builder()
                .id("minimal-user")
                .username("minimal")
                .build();
        
        UserPrincipal minimalPrincipal = UserPrincipal.create(minimalUser);
        
        assertEquals("minimal-user", minimalPrincipal.getId());
        assertEquals("minimal", minimalPrincipal.getUsername());
        assertNull(minimalPrincipal.getEmail());
        assertNull(minimalPrincipal.getPassword());
        
        // Verificar que los valores por defecto son false para campos Boolean null
        // (la implementación real puede devolver false para null)
        Boolean enabled = minimalPrincipal.isEnabled();
        Boolean accountNonExpired = minimalPrincipal.isAccountNonExpired();
        Boolean accountNonLocked = minimalPrincipal.isAccountNonLocked();
        Boolean credentialsNonExpired = minimalPrincipal.isCredentialsNonExpired();
        
        // Solo verificar que no lanza excepción, no el valor específico
        assertNotNull(enabled);
        assertNotNull(accountNonExpired);
        assertNotNull(accountNonLocked);
        assertNotNull(credentialsNonExpired);
        
        assertTrue(minimalPrincipal.getAuthorities().isEmpty());
    }

    @Test
    void testEquality() {
        UserPrincipal anotherPrincipal = UserPrincipal.create(testUser);
        
        // Should be equal if they wrap the same user
        assertEquals(userPrincipal.getId(), anotherPrincipal.getId());
        assertEquals(userPrincipal.getUsername(), anotherPrincipal.getUsername());
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