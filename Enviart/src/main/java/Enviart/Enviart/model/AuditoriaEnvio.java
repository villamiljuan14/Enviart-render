package Enviart.Enviart.model;

import Enviart.Enviart.util.enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_envios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAuditoria;

    @ManyToOne
    @JoinColumn(name = "envio_id", nullable = false)
    private Envio envio;

    @Column(name = "estado_anterior")
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estadoAnterior;

    @Column(name = "estado_nuevo")
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estadoNuevo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @PrePersist
    protected void onCreate() {
        fechaCambio = LocalDateTime.now();
    }
}
