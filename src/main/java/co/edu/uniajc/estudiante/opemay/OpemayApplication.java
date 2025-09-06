package co.edu.uniajc.estudiante.opemay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class OpemayApplication {

    private static final Logger logger = LoggerFactory.getLogger(OpemayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OpemayApplication.class, args);
    }

    @EventListener
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        logger.info("🚀 Servidor corriendo en: http://localhost:{}", port);
    }
}
