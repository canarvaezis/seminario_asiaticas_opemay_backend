package co.edu.uniajc.estudiante.opemayfruitshop.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.cloud.firestore.Firestore;

/**
 * Configuración específica para pruebas de FruitShop
 */
@TestConfiguration
@Profile("test")
public class TestFruitShopConfig {

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Primary
    public Firestore mockFirestore() {
        return Mockito.mock(Firestore.class);
    }
}