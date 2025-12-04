package Enviart.Enviart.repository;

import Enviart.Enviart.model.optimizada.TrackingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar ubicaciones de tracking GPS
 */
@Repository
public interface TrackingLocationRepository extends JpaRepository<TrackingLocation, Integer> {

    /**
     * Obtener todas las ubicaciones de un pedido ordenadas por timestamp
     * descendente
     * 
     * @param pedidoId ID del envío
     * @return Lista de ubicaciones, la más reciente primero
     */
    List<TrackingLocation> findByPedido_IdEnvioOrderByTimestampDesc(Integer pedidoId);

    /**
     * Obtener la última ubicación registrada de un pedido
     * 
     * @param pedidoId ID del envío
     * @return Optional con la ubicación más reciente
     */
    @Query("SELECT t FROM TrackingLocation t WHERE t.pedido.idEnvio = :pedidoId " +
            "ORDER BY t.timestamp DESC LIMIT 1")
    Optional<TrackingLocation> findLatestByPedidoId(@Param("pedidoId") Integer pedidoId);

    /**
     * Obtener ubicaciones de un pedido en un rango de tiempo
     * Útil para análisis y visualización de rutas históricas
     */
    List<TrackingLocation> findByPedido_IdEnvioAndTimestampBetween(
            Integer pedidoId,
            LocalDateTime start,
            LocalDateTime end);

    /**
     * Contar ubicaciones registradas para un pedido
     * Útil para estadísticas
     */
    long countByPedido_IdEnvio(Integer pedidoId);
}
