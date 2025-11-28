package Enviart.Enviart.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteDTO {

    @NotNull(message = "El tipo de servicio es requerido")
    private Integer tipoServicioId;

    @NotNull(message = "El peso es requerido")
    @DecimalMin(value = "0.1", message = "El peso debe ser mayor a 0")
    @DecimalMax(value = "1000.0", message = "El peso no puede exceder 1000 kg")
    private BigDecimal pesoKg;

    @NotNull(message = "El largo es requerido")
    @DecimalMin(value = "1.0", message = "El largo debe ser mayor a 0")
    @DecimalMax(value = "500.0", message = "El largo no puede exceder 500 cm")
    private BigDecimal largoCm;

    @NotNull(message = "El ancho es requerido")
    @DecimalMin(value = "1.0", message = "El ancho debe ser mayor a 0")
    @DecimalMax(value = "500.0", message = "El ancho no puede exceder 500 cm")
    private BigDecimal anchoCm;

    @NotNull(message = "El alto es requerido")
    @DecimalMin(value = "1.0", message = "El alto debe ser mayor a 0")
    @DecimalMax(value = "500.0", message = "El alto no puede exceder 500 cm")
    private BigDecimal altoCm;

    @DecimalMin(value = "0.0", message = "El valor declarado no puede ser negativo")
    private BigDecimal valorDeclarado;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcionContenido;
}
