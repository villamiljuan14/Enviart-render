package Enviart.Enviart.model;

import java.time.LocalDateTime;

import Enviart.Enviart.util.encryption.EncryptedStringConverter;
import Enviart.Enviart.util.enums.TipoDocumento;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idUsuario;

    @Column(name = "tipo_documento", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Column(name = "doc_usuario", nullable = false, length = 255, unique = true)
    @NotBlank(message = "El documento de usuario es obligatorio")
    @Convert(converter = EncryptedStringConverter.class)
    private String docUsuario;

    @Column(name = "primer_nombre", nullable = false, length = 80)
    @NotBlank(message = "El primer nombre es obligatorio")
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 80)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false, length = 80)
    @NotBlank(message = "El primer apellido es obligatorio")
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 80)
    private String segundoApellido;

    @Column(name = "telefono", nullable = false, length = 255)
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Teléfono inválido")
    @Convert(converter = EncryptedStringConverter.class)
    private String telefono;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    @Email(message = "Email inválido")
    private String email;

    @Column(name = "contrasena_usuario", nullable = false, length = 255)
    private String contrasenaUsuario;

    @Column(name = "estado_usuario", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean estadoUsuario = true;

    @Column(name = "two_factor_secret", length = 255)
    private String twoFactorSecret;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id_rol", nullable = false)
    private Rol rol;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
