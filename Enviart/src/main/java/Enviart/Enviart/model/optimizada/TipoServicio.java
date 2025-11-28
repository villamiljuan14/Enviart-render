package Enviart.Enviart.model.optimizada;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "TIPO_SERVICIO_ENVIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoServicio;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "costo_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoBase;

    @Column(name = "peso_max", precision = 6, scale = 2)
    private BigDecimal pesoMax;

    @Column(name = "tiempo_entrega_est", length = 50)
    private String tiempoEntregaEst;
}
