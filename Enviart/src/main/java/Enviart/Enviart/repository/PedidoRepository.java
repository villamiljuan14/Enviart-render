package Enviart.Enviart.repository;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.optimizada.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Buscar pedidos por usuario
    List<Pedido> findByUsuario(Usuario usuario);

    // Buscar pedidos por usuario ordenados por fecha descendente
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(Usuario usuario);

    // Buscar pedidos por estado
    @Query("SELECT p FROM Pedido p WHERE p.estado.nombre = :nombreEstado")
    List<Pedido> findByEstadoNombre(@Param("nombreEstado") String nombreEstado);

    // Buscar pedidos por rango de fechas
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :inicio AND :fin")
    List<Pedido> findByFechaPedidoBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Buscar pedidos por usuario y estado
    @Query("SELECT p FROM Pedido p WHERE p.usuario = :usuario AND p.estado.nombre = :nombreEstado")
    List<Pedido> findByUsuarioAndEstadoNombre(@Param("usuario") Usuario usuario,
            @Param("nombreEstado") String nombreEstado);

    // Contar pedidos por usuario
    long countByUsuario(Usuario usuario);

    long countByUsuario_IdUsuario(Integer usuarioId);

    // Obtener últimos N pedidos
    List<Pedido> findTop10ByOrderByFechaPedidoDesc();

    // Buscar pedidos asignados a un conductor (a través de rutas)
    @Query(value = "SELECT p.* FROM PEDIDO p " +
            "JOIN pedido_has_ruta phr ON p.idPedido = phr.pedido_id_pedido " +
            "JOIN RUTA r ON phr.ruta_id_ruta = r.idRuta " +
            "WHERE r.usuario_id_conductor = :conductorId", nativeQuery = true)
    List<Pedido> findPedidosByConductorId(@Param("conductorId") Integer conductorId);
}
