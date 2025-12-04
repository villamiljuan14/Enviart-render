package Enviart.Enviart.model.optimizada;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAGO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "pedido_id_pedido", nullable = false)
    @NotNull(message = "El pedido es obligatorio")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "tipo_pago_id", nullable = false)
    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    @NotNull(message = "La fecha de pago es obligatoria")
    private LocalDateTime fechaPago;
}
