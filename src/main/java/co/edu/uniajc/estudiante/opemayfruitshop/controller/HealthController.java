package co.edu.uniajc.estudiante.opemayfruitshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "🚀 Opem-ay Fruit Shop API está corriendo!";
    }

    @GetMapping("/health")
    public String health() {
        return "✅ El servidor está activo y funcionando correctamente.";
    }
}
