package Enviart.Enviart.model.optimizada;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CAT_ESTADO_PEDIDO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstado;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    private Integer orden;
}
