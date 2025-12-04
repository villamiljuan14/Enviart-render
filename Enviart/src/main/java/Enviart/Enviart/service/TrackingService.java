package Enviart.Enviart.service;

import Enviart.Enviart.dto.LocationUpdateDTO;
import Enviart.Enviart.dto.PedidoTrackingDTO;
import Enviart.Enviart.model.Envio;
import Enviart.Enviart.model.optimizada.TrackingLocation;
import Enviart.Enviart.repository.EnvioRepository;
import Enviart.Enviart.repository.TrackingLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para gestionar tracking GPS en tiempo real
 */
@Service
public class TrackingService {

    private final TrackingLocationRepository trackingLocationRepository;
    private final EnvioRepository envioRepository;

    @Autowired
    public TrackingService(TrackingLocationRepository trackingLocationRepository,
            EnvioRepository envioRepository) {
        this.trackingLocationRepository = trackingLocationRepository;
        this.envioRepository = envioRepository;
    }

    /**
     * Guardar nueva ubicación GPS y actualizar cache del envío
     * 
     * @param locationDTO Datos de ubicación recibidos
     * @return TrackingLocation guardado
     */
    @Transactional
    public TrackingLocation guardarUbicacion(LocationUpdateDTO locationDTO) {
        // Buscar el envío
        Envio envio = envioRepository.findById(locationDTO.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + locationDTO.getPedidoId()));

        // Crear nuevo registro de tracking
        TrackingLocation tracking = TrackingLocation.builder()
                .pedido(envio)
                .latitud(locationDTO.getLatitud())
                .longitud(locationDTO.getLongitud())
                .velocidad(locationDTO.getVelocidad())
                .timestamp(LocalDateTime.now())
                .build();

        // Guardar tracking
        TrackingLocation saved = trackingLocationRepository.save(tracking);

        // Opcional: Actualizar cache en envio si la tabla tiene las columnas
        // envio.setUltimaLatitud(locationDTO.getLatitud());
        // envio.setUltimaLongitud(locationDTO.getLongitud());
        // envio.setUltimaActualizacion(LocalDateTime.now());
        // envioRepository.save(envio);

        return saved;
    }

    /**
     * Obtener datos completos de tracking de un pedido
     * Incluye información del pedido + última ubicación conocida
     * 
     * @param pedidoId ID del envío
     * @return DTO con datos completos de tracking
     */
    public PedidoTrackingDTO obtenerTrackingPedido(Integer pedidoId) {
        Envio envio = envioRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        Optional<TrackingLocation> ultimaUbicacion = trackingLocationRepository.findLatestByPedidoId(pedidoId);

        PedidoTrackingDTO.PedidoTrackingDTOBuilder builder = PedidoTrackingDTO.builder()
                .pedidoId(envio.getIdEnvio())
                .numeroGuia(envio.getNumeroGuia())
                .estado(envio.getEstado().name());

        // Agregar datos de ubicación si existen
        if (ultimaUbicacion.isPresent()) {
            TrackingLocation loc = ultimaUbicacion.get();
            builder.latitud(loc.getLatitud())
                    .longitud(loc.getLongitud())
                    .velocidad(loc.getVelocidad())
                    .ultimaActualizacion(loc.getTimestamp());
        }

        // Agregar direcciones de origen y destino
        if (envio.getRemitenteDireccion() != null) {
            builder.direccionOrigen(envio.getRemitenteDireccion() + ", " + envio.getRemitenteCiudad());
        }
        if (envio.getDestinatarioDireccion() != null) {
            builder.direccionDestino(envio.getDestinatarioDireccion() + ", " + envio.getDestinatarioCiudad());
        }

        return builder.build();
    }

    /**
     * Obtener historial cronológico de ubicaciones de un pedido
     * 
     * @param pedidoId ID del envío
     * @return Lista de ubicaciones ordenadas por timestamp descendente
     */
    public List<TrackingLocation> obtenerHistorialUbicaciones(Integer pedidoId) {
        return trackingLocationRepository.findByPedido_IdEnvioOrderByTimestampDesc(pedidoId);
    }

    /**
     * Obtener ubicaciones en un rango de tiempo específico
     * 
     * @param pedidoId ID del envío
     * @param inicio   Timestamp de inicio
     * @param fin      Timestamp de fin
     * @return Lista de ubicaciones en el rango
     */
    public List<TrackingLocation> obtenerUbicacionesEnRango(Integer pedidoId,
            LocalDateTime inicio,
            LocalDateTime fin) {
        return trackingLocationRepository.findByPedido_IdEnvioAndTimestampBetween(
                pedidoId, inicio, fin);
    }

    /**
     * Obtener estadísticas de tracking para un pedido
     * 
     * @param pedidoId ID del envío
     * @return Número total de puntos GPS registrados
     */
    public long obtenerCantidadPuntosGPS(Integer pedidoId) {
        return trackingLocationRepository.countByPedido_IdEnvio(pedidoId);
    }
}
