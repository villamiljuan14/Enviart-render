package Enviart.Enviart.repository;

import Enviart.Enviart.model.optimizada.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoServicioRepository extends JpaRepository<TipoServicio, Integer> {

    // Buscar tipo de servicio por nombre
    Optional<TipoServicio> findByNombre(String nombre);
}
