package Enviart.Enviart.repository;

import Enviart.Enviart.model.optimizada.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Integer> {

    // Buscar estado por nombre
    Optional<EstadoPedido> findByNombre(String nombre);
}
