package co.edu.uniajc.estudiante.opemayfruitshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id; // UID
    private String email;
    private String name;
    private String address;
    private String phone;
    public String password;
}
