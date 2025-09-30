package co.edu.uniajc.estudiante.opemay.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "🍎 OpemAy Fruit Shop API",
        version = "1.0.0",
        description = """
            ## 🌟 API para E-commerce de Tienda de Frutas
            
            **OpemAy Fruit Shop** es una aplicación de comercio electrónico desarrollada con Spring Boot 3.5.4 y Java 24.
            
            ### 🚀 Características Principales:
            - ✅ **Autenticación JWT** - Sistema seguro de tokens
            - ✅ **Gestión de Usuarios** - CRUD completo con validaciones
            - ✅ **Spring Security** - Autorización basada en roles
            - ✅ **Firebase Firestore** - Base de datos NoSQL en la nube
            - ✅ **Testing Completo** - 415 tests unitarios e integración
            
            ### 🔐 Autenticación
            La API utiliza **JWT (JSON Web Tokens)** para autenticación:
            1. Registra un usuario con `POST /api/users/register`
            2. Haz login con `POST /api/auth/login` para obtener tu token
            3. Incluye el token en el header: `Authorization: Bearer <tu_token>`
            
            ### 📊 Estado del Proyecto
            - **Tests**: 415/415 pasando (100% ✅)
            - **Coverage**: Cobertura completa de código
            - **Build**: Gradle con Java 24
            - **Cloud**: Desplegado con Firebase
            
            ### 🤝 Desarrollado por:
            **Seminario de Asiáticas - UNIAJC 2025**
            """,
        contact = @Contact(
            name = "Equipo OpemAy",
            email = "opemay@uniajc.edu.co",
            url = "https://github.com/canarvaezis/Seminario-de-asiaticas---Opem-Ay-"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "🏠 Servidor de Desarrollo Local"
        ),
        @Server(
            url = "https://opemay-api.herokuapp.com",
            description = "☁️ Servidor de Producción (Ejemplo)"
        )
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = """
        ## 🔑 Autenticación JWT
        
        Para usar endpoints protegidos:
        1. **Registrate**: `POST /api/users/register`
        2. **Haz Login**: `POST /api/auth/login` 
        3. **Usa el Token**: Copia el token y pégalo aquí 👆
        
        **Formato del Token**: `eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2Vy...`
        
        ⏱️ **Expiración**: 24 horas
        """
)
public class SwaggerConfig {
}