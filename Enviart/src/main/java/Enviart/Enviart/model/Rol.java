package Enviart.Enviart.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false, length = 50)
    private String nombreRol;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_rol", length = 50)
    private Enviart.Enviart.util.enums.TipoRol tipoRol;
}
