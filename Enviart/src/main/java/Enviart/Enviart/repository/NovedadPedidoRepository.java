package Enviart.Enviart.repository;

import Enviart.Enviart.model.optimizada.NovedadPedido;
import Enviart.Enviart.model.optimizada.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NovedadPedidoRepository extends JpaRepository<NovedadPedido, Integer> {
    List<NovedadPedido> findByPedido(Pedido pedido);

    List<NovedadPedido> findByTipoNovedad(String tipoNovedad);

    List<NovedadPedido> findByPedidoOrderByFechaNovedadDesc(Pedido pedido);

    List<NovedadPedido> findAllByOrderByFechaNovedadDesc();
}
