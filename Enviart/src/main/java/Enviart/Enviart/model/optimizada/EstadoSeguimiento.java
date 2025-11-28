package Enviart.Enviart.model.optimizada;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CAT_ESTADO_SEGUIMIENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoSeguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstadoSeguimiento;

    @Column(nullable = false, unique = true, length = 75)
    private String nombre;

    @Column(name = "tipo_alerta", length = 20)
    private String tipoAlerta;
}
