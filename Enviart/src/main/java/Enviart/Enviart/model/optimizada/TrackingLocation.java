package Enviart.Enviart.model.optimizada;

import Enviart.Enviart.model.Envio;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad para almacenar ubicaciones GPS del tracking en tiempo real
 * Cada registro representa una "ping" de ubicación del mensajero
 */
@Entity
@Table(name = "tracking_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tracking")
    private Integer idTracking;

    /**
     * Pedido/Envío al que pertenece esta ubicación
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id_pedido", nullable = false)
    private Envio pedido;

    /**
     * Latitud en grados decimales (-90 a 90)
     * Ejemplo: 4.7110 (Bogotá)
     */
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitud;

    /**
     * Longitud en grados decimales (-180 a 180)
     * Ejemplo: -74.0721 (Bogotá)
     */
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitud;

    /**
     * Velocidad del vehículo en km/h
     * Opcional, puede ser null si no está disponible
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal velocidad;

    /**
     * Dirección legible obtenida por geocoding reverso (opcional)
     * Ejemplo: "Calle 72 #10-30, Bogotá"
     */
    @Column(length = 255)
    private String direccion;

    /**
     * Momento exacto en que se capturó esta ubicación
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Auto-asignar timestamp si no se proporciona
     */
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
