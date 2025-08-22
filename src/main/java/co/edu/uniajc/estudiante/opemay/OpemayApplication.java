package co.edu.uniajc.estudiante.opemay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class OpemayApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpemayApplication.class, args);
    }
}
