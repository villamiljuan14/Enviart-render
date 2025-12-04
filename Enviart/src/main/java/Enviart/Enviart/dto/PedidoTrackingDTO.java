package Enviart.Enviart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO completo para enviar información de tracking al cliente
 * Incluye tanto datos del pedido como la última ubicación conocida
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoTrackingDTO {

    /**
     * ID del pedido
     */
    private Integer pedidoId;

    /**
     * Número de guía del pedido para mostrar al usuario
     */
    private String numeroGuia;

    /**
     * Estado actual del pedido (EN_TRANSITO, ENTREGADO, etc.)
     */
    private String estado;

    /**
     * Última latitud conocida
     */
    private BigDecimal latitud;

    /**
     * Última longitud conocida
     */
    private BigDecimal longitud;

    /**
     * Última velocidad registrada en km/h
     */
    private BigDecimal velocidad;

    /**
     * Timestamp de la última actualización GPS
     */
    private LocalDateTime ultimaActualizacion;

    /**
     * Dirección de origen del pedido
     */
    private String direccionOrigen;

    /**
     * Dirección de destino del pedido
     */
    private String direccionDestino;
}
