package co.edu.uniajc.estudiante.opemay;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import co.edu.uniajc.estudiante.opemay.config.FirestoreTestConfig;

@SpringBootTest
@Import(FirestoreTestConfig.class)
@ActiveProfiles("test")
class OpemayApplicationTests {

    @Test
    void contextLoads() {
    }
}
