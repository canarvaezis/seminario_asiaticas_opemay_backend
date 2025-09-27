package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.uniajc.estudiante.opemay.config.TestFirebaseConfig;
import co.edu.uniajc.estudiante.opemay.config.TestSecurityConfig;

@WebMvcTest(HomeController.class)
@ActiveProfiles("test")
@Import({TestFirebaseConfig.class, TestSecurityConfig.class})
@SuppressWarnings("deprecation") // Suppress MockBean deprecation warnings
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean  
    private co.edu.uniajc.estudiante.opemay.Service.JwtService jwtService;

    @Test
    @WithMockUser
    void testHome() throws Exception {
        mockMvc.perform(get("/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("¡Bienvenido a Opemay API!"))
                .andExpect(jsonPath("$.description").value("API de comercio electrónico con autenticación JWT"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.status").value("API funcionando correctamente"))
                .andExpect(jsonPath("$.endpoints").exists())
                .andExpect(jsonPath("$.endpoints.auth").exists())
                .andExpect(jsonPath("$.endpoints.products").exists())
                .andExpect(jsonPath("$.endpoints.users").exists())
                .andExpect(jsonPath("$.endpoints.auth.register").value("POST /api/auth/register"))
                .andExpect(jsonPath("$.endpoints.auth.login").value("POST /api/auth/login"));
    }

    @Test
    @WithMockUser
    void testStatus() throws Exception {
        mockMvc.perform(get("/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testControllerExists() {
        HomeController controller = new HomeController();
        assertNotNull(controller);
    }

    @Test
    @WithMockUser
    void testHomeEndpointStructure() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.products.getAll").value("GET /api/products/all"))
                .andExpect(jsonPath("$.endpoints.products.getById").value("GET /api/products/{id}"))
                .andExpect(jsonPath("$.endpoints.products.create").value("POST /api/products/save"))
                .andExpect(jsonPath("$.endpoints.users.getAll").value("GET /api/users/all (requiere autenticación)"))
                .andExpect(jsonPath("$.endpoints.users.getById").value("GET /api/users/{id} (requiere autenticación)"))
                .andExpect(jsonPath("$.endpoints.users.update").value("PUT /api/users/{id} (requiere autenticación)"))
                .andExpect(jsonPath("$.endpoints.users.delete").value("DELETE /api/users/{id} (requiere autenticación)"));
    }

    @Test
    @WithMockUser
    void testStatusTimestamp() throws Exception {
        // Verificar que el timestamp se genera correctamente
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser
    void testHomeResponseType() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("¡Bienvenido a Opemay API!"));
    }

    @Test
    @WithMockUser  
    void testStatusResponseType() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}