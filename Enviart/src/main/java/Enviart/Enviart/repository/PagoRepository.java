package Enviart.Enviart.repository;

import Enviart.Enviart.model.optimizada.Pago;
import Enviart.Enviart.model.optimizada.Pedido;
import Enviart.Enviart.model.optimizada.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByPedido(Pedido pedido);

    List<Pago> findByTipoPago(TipoPago tipoPago);

    List<Pago> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Pago> findByPedidoOrderByFechaPagoDesc(Pedido pedido);

    // Métodos para contar dependencias antes de eliminar
    long countByTipoPago_IdTipoPago(Integer tipoPagoId);

    long countByPedido_IdPedido(Integer pedidoId);
}
