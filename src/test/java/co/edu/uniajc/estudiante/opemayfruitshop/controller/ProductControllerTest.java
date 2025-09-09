package co.edu.uniajc.estudiante.opemayfruitshop.controller;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;
import co.edu.uniajc.estudiante.opemayfruitshop.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("GET /api/products devuelve lista de productos")
    void testGetAll() throws Exception {
        Product p1 = new Product("1", "Manzana", 1.5, 100);
        Product p2 = new Product("2", "Pera", 2.0, 50);
        List<Product> products = Arrays.asList(p1, p2);

        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
               .andExpect(status().isOk())
               .andExpect(content().json("[{'id':'1','name':'Manzana','price':1.5,'stock':100},{'id':'2','name':'Pera','price':2.0,'stock':50}]"));
    }

    @Test
    @DisplayName("GET /api/products/{id} devuelve producto existente")
    void testGetByIdExists() throws Exception {
        Product p = new Product("1", "Manzana", 1.5, 100);
        when(productService.findById("1")).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/products/1"))
               .andExpect(status().isOk())
               .andExpect(content().json("{'id':'1','name':'Manzana','price':1.5,'stock':100}"));
    }

    @Test
    @DisplayName("GET /api/products/{id} devuelve 404 si no existe")
    void testGetByIdNotFound() throws Exception {
        when(productService.findById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/1"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/products crea un producto")
    void testCreateProduct() throws Exception {
        Product p = new Product("1", "Manzana", 1.5, 100);
        when(productService.save(any(Product.class))).thenReturn(p);

        String json = "{\"id\":\"1\",\"name\":\"Manzana\",\"price\":1.5,\"stock\":100}";

        mockMvc.perform(post("/api/products")
               .contentType(MediaType.APPLICATION_JSON)
               .content(json))
               .andExpect(status().isOk())
               .andExpect(content().json(json));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} elimina un producto")
    void testDeleteProduct() throws Exception {
        Mockito.doNothing().when(productService).delete("1");

        mockMvc.perform(delete("/api/products/1"))
               .andExpect(status().isNoContent());
    }
}
