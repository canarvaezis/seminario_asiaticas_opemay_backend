package co.edu.uniajc.estudiante.opemayfruitshop;

import co.edu.uniajc.estudiante.opemayfruitshop.service.ProductService;
import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class OpemAyFruitShopApplicationTests {

    @MockBean
    private Firestore firestore; // Mock de Firebase

    @MockBean
    private ProductService productService; // Mock del servicio

    @Test
    void contextLoads() {
        // Solo verificamos que el contexto se levante
    }
}
