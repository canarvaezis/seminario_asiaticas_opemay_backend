package co.edu.uniajc.estudiante.opemay.config;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.cloud.Timestamp;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        
        SimpleModule timestampModule = new SimpleModule();
        timestampModule.addSerializer(Timestamp.class, new TimestampSerializer());
        timestampModule.addDeserializer(Timestamp.class, new TimestampDeserializer());
        
        mapper.registerModule(timestampModule);
        return mapper;
    }
    
    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        
        SimpleModule timestampModule = new SimpleModule();
        timestampModule.addSerializer(Timestamp.class, new TimestampSerializer());
        timestampModule.addDeserializer(Timestamp.class, new TimestampDeserializer());
        
        builder.modules(timestampModule);
        return builder;
    }
    
    public static class TimestampSerializer extends StdSerializer<Timestamp> {
        
        public TimestampSerializer() {
            this(null);
        }
        
        public TimestampSerializer(Class<Timestamp> t) {
            super(t);
        }
        
        @Override
        public void serialize(Timestamp timestamp, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (timestamp != null) {
                // Convertir a ISO-8601 string
                gen.writeString(DateTimeFormatter.ISO_INSTANT.format(timestamp.toSqlTimestamp().toInstant()));
            } else {
                gen.writeNull();
            }
        }
    }
    
    public static class TimestampDeserializer extends StdDeserializer<Timestamp> {
        
        public TimestampDeserializer() {
            this(null);
        }
        
        public TimestampDeserializer(Class<?> vc) {
            super(vc);
        }
        
        @Override
        public Timestamp deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String timestamp = p.getText();
            if (timestamp == null || timestamp.trim().isEmpty()) {
                return null;
            }
            
            try {
                // Intentar parsear como ISO-8601
                Instant instant = Instant.parse(timestamp);
                return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
            } catch (DateTimeParseException _) {
                try {
                    // Fallback: intentar parsear como segundos epoch
                    long epochSeconds = Long.parseLong(timestamp);
                    return Timestamp.ofTimeSecondsAndNanos(epochSeconds, 0);
                } catch (NumberFormatException ex) {
                    throw new IOException("No se puede parsear el timestamp: " + timestamp, ex);
                }
            }
        }
    }
}