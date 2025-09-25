package co.edu.uniajc.estudiante.opemay;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import co.edu.uniajc.estudiante.opemay.config.TestConfig;

@SpringBootTest(classes = OpemayApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "firebase.enabled=false"
})
@Import(TestConfig.class)
class OpemayApplicationTests {

	@Test
	void contextLoads() {
	}

}
