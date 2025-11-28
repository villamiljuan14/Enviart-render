package Enviart.Enviart.repository;

import Enviart.Enviart.model.optimizada.Paquete;
import Enviart.Enviart.model.optimizada.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {

    // Buscar paquetes por pedido
    List<Paquete> findByPedido(Pedido pedido);

    // Contar paquetes por pedido
    long countByPedido(Pedido pedido);
}
