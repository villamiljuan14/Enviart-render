package Enviart.Enviart.repository;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.optimizada.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {

    // Buscar direcciones por usuario
    List<Direccion> findByUsuario(Usuario usuario);

    // Contar direcciones por usuario
    long countByUsuario(Usuario usuario);
}
