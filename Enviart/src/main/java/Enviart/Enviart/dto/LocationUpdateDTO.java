package Enviart.Enviart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO para recibir actualizaciones de ubicación desde el cliente
 * Usado cuando el mensajero envía su ubicación GPS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationUpdateDTO {

    /**
     * ID del pedido que se está rastreando
     */
    private Integer pedidoId;

    /**
     * Latitud en grados decimales
     */
    private BigDecimal latitud;

    /**
     * Longitud en grados decimales
     */
    private BigDecimal longitud;

    /**
     * Velocidad del vehículo en km/h (opcional)
     */
    private BigDecimal velocidad;

    /**
     * Timestamp ISO 8601 de cuando se capturó la ubicación
     * Ejemplo: "2024-12-03T14:30:00"
     */
    private String timestamp;
}
