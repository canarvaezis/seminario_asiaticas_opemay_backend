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
        
        UserPrincipal principalWithNullRoles = UserPrincipal.create(userWithNullRoles);
        Collection<? extends GrantedAuthority> authorities = principalWithNullRoles.getAuthorities();
        
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
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
        
        // Test con cuenta expirada
        testUser.setAccountNonExpired(false);
        assertFalse(userPrincipal.isAccountNonExpired());
        
        // Test con null
        testUser.setAccountNonExpired(null);
        assertFalse(userPrincipal.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(userPrincipal.isAccountNonLocked());
        
        // Test con cuenta bloqueada
        testUser.setAccountNonLocked(false);
        assertFalse(userPrincipal.isAccountNonLocked());
        
        // Test con null
        testUser.setAccountNonLocked(null);
        assertFalse(userPrincipal.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
        
        // Test con credenciales expiradas
        testUser.setCredentialsNonExpired(false);
        assertFalse(userPrincipal.isCredentialsNonExpired());
        
        // Test con null
        testUser.setCredentialsNonExpired(null);
        assertFalse(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(userPrincipal.isEnabled());
        
        // Test con usuario deshabilitado
        testUser.setEnabled(false);
        assertFalse(userPrincipal.isEnabled());
        
        // Test con null
        testUser.setEnabled(null);
        assertFalse(userPrincipal.isEnabled());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", userPrincipal.getUsername());
        
        // Test con username null
        testUser.setUsername(null);
        assertNull(userPrincipal.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("encodedPassword", userPrincipal.getPassword());
        
        // Test con password null
        testUser.setPassword(null);
        assertNull(userPrincipal.getPassword());
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
        
        // Valores por defecto should be false para campos null
        assertFalse(minimalPrincipal.isEnabled());
        assertFalse(minimalPrincipal.isAccountNonExpired());
        assertFalse(minimalPrincipal.isAccountNonLocked());
        assertFalse(minimalPrincipal.isCredentialsNonExpired());
        
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
        // El toString debería contener información útil pero no la contraseña
        assertTrue(toString.contains("testuser") || toString.contains("user-123"));
        assertFalse(toString.contains("encodedPassword")); // No debe exponer la contraseña
    }
}