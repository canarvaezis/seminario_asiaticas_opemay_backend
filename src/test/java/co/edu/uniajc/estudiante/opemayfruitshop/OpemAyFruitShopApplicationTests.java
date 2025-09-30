package co.edu.uniajc.estudiante.opemayfruitshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import co.edu.uniajc.estudiante.opemay.OpemayApplication;

@SpringBootTest(classes = OpemayApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "firebase.enabled=false"
})
@Import(co.edu.uniajc.estudiante.opemayfruitshop.config.TestFruitShopConfig.class)
class OpemAyFruitShopApplicationTests {

    @Test
    void contextLoads() {
    }

}
