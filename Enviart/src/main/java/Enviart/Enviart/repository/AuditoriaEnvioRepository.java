package Enviart.Enviart.repository;

import Enviart.Enviart.model.AuditoriaEnvio;
import Enviart.Enviart.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaEnvioRepository extends JpaRepository<AuditoriaEnvio, Integer> {

    List<AuditoriaEnvio> findByEnvioOrderByFechaCambioDesc(Envio envio);

    List<AuditoriaEnvio> findByEnvio_IdEnvioOrderByFechaCambioDesc(Integer idEnvio);
}
