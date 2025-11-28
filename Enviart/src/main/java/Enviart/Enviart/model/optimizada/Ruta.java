package Enviart.Enviart.model.optimizada;

import Enviart.Enviart.model.Usuario;
import jakarta.persistence.*;
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
    private String nombreRuta;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "usuario_id_conductor", nullable = false)
    private Usuario conductor;

    @Column(name = "status_ruta", nullable = false, length = 20)
    private String statusRuta;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;
}
