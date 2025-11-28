package Enviart.Enviart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoDTO {

    @NotNull(message = "La dirección de origen es requerida")
    @Valid
    private DireccionDTO direccionOrigen;

    @NotNull(message = "La dirección de destino es requerida")
    @Valid
    private DireccionDTO direccionDestino;

    @NotEmpty(message = "Debe incluir al menos un paquete")
    @Valid
    private List<PaqueteDTO> paquetes;

    // Campos opcionales para información adicional
    private String notasEspeciales;

    private Boolean requiereSeguro;
}
