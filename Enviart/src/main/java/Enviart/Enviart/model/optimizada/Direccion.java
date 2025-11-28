package Enviart.Enviart.model.optimizada;

import Enviart.Enviart.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DIRECCION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDireccion;

    @ManyToOne
    @JoinColumn(name = "usuario_id_usuario")
    private Usuario usuario;

    @Column(name = "nombre_contacto", length = 100)
    private String nombreContacto;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(nullable = false, length = 100)
    private String calle;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(name = "codigo_postal", nullable = false, length = 10)
    private String codigoPostal;
}
