package co.edu.uniajc.estudiante.opemay.model;

import java.util.List;

import com.google.cloud.Timestamp;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import co.edu.uniajc.estudiante.opemay.config.JacksonConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class User {
    
    private String id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    
    @Builder.Default
    private Boolean enabled = true;
    
    @Builder.Default
    private Boolean accountNonExpired = true;
    
    @Builder.Default
    private Boolean accountNonLocked = true;
    
    @Builder.Default
    private Boolean credentialsNonExpired = true;
    
    @Builder.Default
    private List<String> roles = List.of("USER");
    
    @Builder.Default
    @JsonSerialize(using = JacksonConfig.TimestampSerializer.class)
    @JsonDeserialize(using = JacksonConfig.TimestampDeserializer.class)
    private Timestamp createdAt = Timestamp.now();
    
    @JsonSerialize(using = JacksonConfig.TimestampSerializer.class)
    @JsonDeserialize(using = JacksonConfig.TimestampDeserializer.class)
    private Timestamp lastLogin;
    
    @JsonSerialize(using = JacksonConfig.TimestampSerializer.class)
    @JsonDeserialize(using = JacksonConfig.TimestampDeserializer.class)
    private Timestamp updatedAt;
}
