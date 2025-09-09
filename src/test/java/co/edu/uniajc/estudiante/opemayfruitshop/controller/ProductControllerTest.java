package co.edu.uniajc.estudiante.opemayfruitshop.controller;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;
import co.edu.uniajc.estudiante.opemayfruitshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Test
    void testGetAll() throws Exception {
        Product product = new Product("1", "Apple", 1.2, 10);
        Mockito.when(service.findAll()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("Apple")));
    }

    @Test
    void testGetByIdFound() throws Exception {
        Product product = new Product("1", "Apple", 1.2, 10);
        Mockito.when(service.findById("1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Apple")));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        Mockito.when(service.findById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        Product product = new Product("1", "Apple", 1.2, 10);
        Mockito.when(service.save(any(Product.class))).thenReturn(product);

        String json = """
            {
                "id": "1",
                "name": "Apple",
                "price": 1.2,
                "stock": 10
            }
            """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Apple")));
    }

    @Test
    void testDelete() throws Exception {
        Mockito.doNothing().when(service).delete("1");

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
