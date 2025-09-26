package co.edu.uniajc.estudiante.opemay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Slf4j  
public class OpemayApplication {

    public static void main(String[] args) {
        log.info("Iniciando aplicación Opemay con Circuit Breaker y Firebase");
        SpringApplication.run(OpemayApplication.class, args);
        log.info("Aplicación Opemay iniciada correctamente");
    }
} 
  