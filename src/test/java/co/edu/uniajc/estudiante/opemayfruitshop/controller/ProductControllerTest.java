package co.edu.uniajc.estudiante.opemayfruitshop.controller;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;
import co.edu.uniajc.estudiante.opemayfruitshop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAll() throws Exception {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple", 100, 2.5),
                new Product("2", "Banana", 200, 1.5)
        );

        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[1].name").value("Banana"));
    }

    @Test
    void testGetByIdFound() throws Exception {
        Product product = new Product("1", "Apple", 100, 2.5);

        when(productService.findById("1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(productService.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct() throws Exception {
        Product product = new Product("1", "Apple", 100, 2.5);

        when(productService.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).delete(eq("1"));

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
