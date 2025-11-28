package Enviart.Enviart.model;

import Enviart.Enviart.util.encryption.EncryptedStringConverter;
import Enviart.Enviart.util.enums.EstadoEnvio;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idEnvio;

    @Column(name = "numero_guia", nullable = false, length = 50, unique = true)
    @NotBlank(message = "El número de guía es obligatorio")
    private String numeroGuia;

    // REMITENTE
    @Column(name = "remitente_nombre", nullable = false, length = 255)
    @NotBlank(message = "El nombre del remitente es obligatorio")
    private String remitenteNombre;

    @Column(name = "remitente_telefono", nullable = false, length = 255)
    @NotBlank(message = "El teléfono del remitente es obligatorio")
    @Convert(converter = EncryptedStringConverter.class)
    private String remitenteTelefono;

    @Column(name = "remitente_direccion", nullable = false, length = 500)
    @NotBlank(message = "La dirección del remitente es obligatoria")
    @Convert(converter = EncryptedStringConverter.class)
    private String remitenteDireccion;

    @Column(name = "remitente_ciudad", nullable = false, length = 100)
    @NotBlank(message = "La ciudad del remitente es obligatoria")
    private String remitenteCiudad;

    // DESTINATARIO
    @Column(name = "destinatario_nombre", nullable = false, length = 255)
    @NotBlank(message = "El nombre del destinatario es obligatorio")
    private String destinatarioNombre;

    @Column(name = "destinatario_telefono", nullable = false, length = 255)
    @NotBlank(message = "El teléfono del destinatario es obligatorio")
    @Convert(converter = EncryptedStringConverter.class)
    private String destinatarioTelefono;

    @Column(name = "destinatario_direccion", nullable = false, length = 500)
    @NotBlank(message = "La dirección del destinatario es obligatoria")
    @Convert(converter = EncryptedStringConverter.class)
    private String destinatarioDireccion;

    @Column(name = "destinatario_ciudad", nullable = false, length = 100)
    @NotBlank(message = "La ciudad del destinatario es obligatoria")
    private String destinatarioCiudad;

    // DETALLES DEL PAQUETE
    @Column(name = "descripcion_contenido", length = 500)
    private String descripcionContenido;

    @Column(name = "peso_kg", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
    private BigDecimal pesoKg;

    @Column(name = "largo_cm", precision = 10, scale = 2)
    private BigDecimal largoCm;

    @Column(name = "ancho_cm", precision = 10, scale = 2)
    private BigDecimal anchoCm;

    @Column(name = "alto_cm", precision = 10, scale = 2)
    private BigDecimal altoCm;

    @Column(name = "valor_declarado", precision = 12, scale = 2)
    private BigDecimal valorDeclarado;

    // ESTADO Y SEGUIMIENTO
    @Column(name = "estado", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado = EstadoEnvio.RECEPCIONADO;

    @Column(name = "tarifa", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "La tarifa es obligatoria")
    private BigDecimal tarifa;

    @Column(name = "fecha_estimada_entrega")
    private LocalDateTime fechaEstimadaEntrega;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    // ASIGNACIÓN
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id")
    private Usuario transportista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_registro_id", nullable = false)
    private Usuario usuarioRegistro;

    // AUDITORÍA
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (numeroGuia == null || numeroGuia.isEmpty()) {
            // Generar número de guía automático
            numeroGuia = "ENV-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
