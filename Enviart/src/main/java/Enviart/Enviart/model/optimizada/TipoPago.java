package Enviart.Enviart.model.optimizada;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CAT_TIPO_PAGO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoPago;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
}
