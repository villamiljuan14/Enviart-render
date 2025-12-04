package Enviart.Enviart.repository;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.optimizada.Ruta;
import Enviart.Enviart.model.optimizada.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    Optional<Ruta> findByNombreRuta(String nombreRuta);

    List<Ruta> findByVehiculo(Vehiculo vehiculo);

    List<Ruta> findByConductor(Usuario conductor);

    // Métodos para contar dependencias antes de eliminar
    long countByVehiculo_IdVehiculo(Integer vehiculoId);

    long countByConductor_IdUsuario(Integer conductorId);

    // Buscar rutas por ID del conductor
    List<Ruta> findByConductor_IdUsuario(Integer conductorId);
}
