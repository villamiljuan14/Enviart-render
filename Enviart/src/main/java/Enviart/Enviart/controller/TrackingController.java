package Enviart.Enviart.controller;

import Enviart.Enviart.dto.LocationUpdateDTO;
import Enviart.Enviart.dto.PedidoTrackingDTO;
import Enviart.Enviart.model.optimizada.TrackingLocation;
import Enviart.Enviart.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar tracking en tiempo real
 * Maneja tanto endpoints WebSocket como REST
 */
@Controller
public class TrackingController {

    private final TrackingService trackingService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public TrackingController(TrackingService trackingService,
            SimpMessagingTemplate messagingTemplate) {
        this.trackingService = trackingService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Endpoint WebSocket para recibir actualizaciones de ubicación
     * El mensajero envía a: /app/location
     * Los clientes suscritos reciben en: /topic/pedido/{pedidoId}
     * 
     * Flujo:
     * 1. Mensajero envía ubicación GPS
     * 2. Se guarda en BD
     * 3. Se hace broadcast a todos los clientes suscritos al pedido
     */
    @MessageMapping("/location")
    public void actualizarUbicacion(LocationUpdateDTO locationDTO) {
        try {
            // Guardar ubicación en base de datos
            TrackingLocation tracking = trackingService.guardarUbicacion(locationDTO);

            // Obtener datos completos del pedido con tracking
            PedidoTrackingDTO trackingData = trackingService.obtenerTrackingPedido(locationDTO.getPedidoId());

            // Broadcast a todos los clientes suscritos al tópico del pedido
            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + locationDTO.getPedidoId(),
                    trackingData);

            System.out.println("✓ Ubicación actualizada para pedido " + locationDTO.getPedidoId() +
                    " - Lat: " + locationDTO.getLatitud() + ", Lon: " + locationDTO.getLongitud());

        } catch (Exception e) {
            System.err.println("✗ Error al actualizar ubicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== REST ENDPOINTS ==========

    /**
     * GET: Obtener tracking actual de un pedido
     * URL: /api/tracking/pedido/{pedidoId}
     */
    @GetMapping("/api/tracking/pedido/{pedidoId}")
    @ResponseBody
    public PedidoTrackingDTO obtenerTracking(@PathVariable Integer pedidoId) {
        return trackingService.obtenerTrackingPedido(pedidoId);
    }

    /**
     * GET: Obtener historial completo de ubicaciones de un pedido
     * URL: /api/tracking/pedido/{pedidoId}/historial
     */
    @GetMapping("/api/tracking/pedido/{pedidoId}/historial")
    @ResponseBody
    public List<TrackingLocation> obtenerHistorial(@PathVariable Integer pedidoId) {
        return trackingService.obtenerHistorialUbicaciones(pedidoId);
    }

    /**
     * GET: Obtener estadísticas de tracking
     * URL: /api/tracking/pedido/{pedidoId}/stats
     */
    @GetMapping("/api/tracking/pedido/{pedidoId}/stats")
    @ResponseBody
    public TrackingStatsDTO obtenerEstadisticas(@PathVariable Integer pedidoId) {
        long puntos = trackingService.obtenerCantidadPuntosGPS(pedidoId);
        return new TrackingStatsDTO(pedidoId, puntos);
    }

    /**
     * DTO simple para estadísticas
     */
    record TrackingStatsDTO(Integer pedidoId, long totalPuntosGPS) {
    }
}
