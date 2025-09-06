package co.edu.uniajc.estudiante.opemay.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String uid;            // UID de Firebase Auth
    private String accountType;    // tipo de cuenta
    private String documentType;   // tipo de documento
    private String documentNumber; // número de documento (único)
    private String fullName;       // nombre completo
    private String address;        // dirección
    private Date createdAt;        // fecha creación
    private Date updatedAt;        // fecha actualización
}
