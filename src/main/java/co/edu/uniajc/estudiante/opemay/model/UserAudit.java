package co.edu.uniajc.estudiante.opemay.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAudit {
    private String id;     // id del documento en audits
    private String action; // acción realizada
    private Date createdAt;
}

