package Enviart.Enviart.repository;

import Enviart.Enviart.model.Rol;
import Enviart.Enviart.util.enums.TipoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByTipoRol(TipoRol tipoRol);
}
