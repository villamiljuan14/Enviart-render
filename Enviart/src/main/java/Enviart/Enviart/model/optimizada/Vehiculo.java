package Enviart.Enviart.model.optimizada;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "VEHICULO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVehiculo;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(length = 50)
    private String modelo;

    @Column(name = "capacidad_volumen", precision = 6, scale = 2)
    private BigDecimal capacidadVolumen;

    @Column(name = "capacidad_peso", precision = 6, scale = 2)
    private BigDecimal capacidadPeso;
}
