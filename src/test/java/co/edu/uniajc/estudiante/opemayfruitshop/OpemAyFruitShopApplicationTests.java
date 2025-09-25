package co.edu.uniajc.estudiante.opemayfruitshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import co.edu.uniajc.estudiante.opemayfruitshop.config.TestFruitShopConfig;

@SpringBootTest(classes = OpemAyFruitShopApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "firebase.enabled=false"
})
@Import(TestFruitShopConfig.class)
class OpemAyFruitShopApplicationTests {

    @Test
    void contextLoads() {
    }

}
