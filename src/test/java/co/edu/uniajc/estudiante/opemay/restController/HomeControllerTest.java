package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void testHomeResponseType() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testStatusResponseType() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}