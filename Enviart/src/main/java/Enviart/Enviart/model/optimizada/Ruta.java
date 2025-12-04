package Enviart.Enviart.model.optimizada;

import Enviart.Enviart.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "RUTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRuta;

    @Column(name = "nombre_ruta", nullable = false, length = 100)
    @NotBlank(message = "El nombre de la ruta es obligatorio")
    private String nombreRuta;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id_vehiculo", nullable = false)
    @NotNull(message = "El vehículo es obligatorio")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "usuario_id_conductor", nullable = false)
    @NotNull(message = "El conductor es obligatorio")
    private Usuario conductor;

    @Column(name = "status_ruta", nullable = false, length = 20)
    @NotBlank(message = "El status de la ruta es obligatorio")
    private String statusRuta;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;
}
