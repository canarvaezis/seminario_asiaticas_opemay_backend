package co.edu.uniajc.estudiante.opemay.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import co.edu.uniajc.estudiante.opemay.Service.JwtService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTestFixed {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String username = "testuser";
        UserDetails userDetails = mock(UserDetails.class);
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        when(jwtService.validateToken(token)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).getUsernameFromToken(token);
        verify(jwtService).validateToken(token);
        verify(userDetailsService).loadUserByUsername(username);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_TokenProcessingError() throws ServletException, IOException {
        // Arrange
        String token = "error.jwt.token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getUsernameFromToken(token)).thenThrow(new RuntimeException("Token processing error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).getUsernameFromToken(token);
        verifyNoInteractions(userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}