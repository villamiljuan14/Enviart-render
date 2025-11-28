package Enviart.Enviart.model.optimizada;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PAQUETE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paquete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaquete;

    @ManyToOne
    @JoinColumn(name = "pedido_id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "tipo_servicio_id", nullable = false)
    private TipoServicio tipoServicio;

    @Column(name = "peso_kg", nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "largo_cm", nullable = false, precision = 6, scale = 2)
    private BigDecimal largoCm;

    @Column(name = "ancho_cm", nullable = false, precision = 6, scale = 2)
    private BigDecimal anchoCm;

    @Column(name = "alto_cm", nullable = false, precision = 6, scale = 2)
    private BigDecimal altoCm;

    @Column(name = "valor_declarado", precision = 10, scale = 2)
    private BigDecimal valorDeclarado;

    @Column(name = "descripcion_contenido", columnDefinition = "TEXT")
    private String descripcionContenido;

    @Column(name = "costo_servicio", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoServicio;
}
