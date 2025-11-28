package Enviart.Enviart.model.optimizada;

import Enviart.Enviart.model.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PEDIDO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    @ManyToOne
    @JoinColumn(name = "usuario_id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "direccion_origen_id", nullable = false)
    private Direccion direccionOrigen;

    @ManyToOne
    @JoinColumn(name = "direccion_destino_id", nullable = false)
    private Direccion direccionDestino;

    @Column(name = "total_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFinal;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @ManyToOne
    @JoinColumn(name = "estado_id_estado", nullable = false)
    private EstadoPedido estado;
}
