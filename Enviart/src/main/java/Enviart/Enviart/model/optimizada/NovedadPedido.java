package Enviart.Enviart.model.optimizada;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOVEDAD_PEDIDO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NovedadPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNovedad;

    @ManyToOne
    @JoinColumn(name = "pedido_id_pedido", nullable = false)
    @NotNull(message = "El pedido es obligatorio")
    private Pedido pedido;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Column(name = "fecha_novedad", nullable = false)
    @NotNull(message = "La fecha de novedad es obligatoria")
    private LocalDateTime fechaNovedad;

    @Column(name = "tipo_novedad", nullable = false, length = 30)
    @NotBlank(message = "El tipo de novedad es obligatorio")
    private String tipoNovedad; // ACTUALIZACION, INCIDENCIA, ENTREGA, OTRO
}
