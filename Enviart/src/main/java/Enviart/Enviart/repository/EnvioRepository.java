package Enviart.Enviart.repository;

import Enviart.Enviart.model.Envio;
import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.util.enums.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {

    Optional<Envio> findByNumeroGuia(String numeroGuia);

    List<Envio> findByEstado(EstadoEnvio estado);

    List<Envio> findByTransportista(Usuario transportista);

    List<Envio> findByUsuarioRegistro(Usuario usuarioRegistro);

    List<Envio> findByDestinatarioCiudad(String ciudad);

    List<Envio> findByRemitenteCiudad(String ciudad);

    // Total Revenue (Sum of tarifa)
    @Query("SELECT SUM(e.tarifa) FROM Envio e")
    BigDecimal sumTotalTarifa();

    // Monthly Revenue (Grouped by Month)
    @Query("SELECT MONTH(e.createdAt), SUM(e.tarifa) FROM Envio e WHERE YEAR(e.createdAt) = YEAR(CURRENT_DATE) GROUP BY MONTH(e.createdAt)")
    List<Object[]> findMonthlyRevenue();

    // Shipment Status Counts
    @Query("SELECT e.estado, COUNT(e) FROM Envio e GROUP BY e.estado")
    List<Object[]> countByEstado();

    // Top Cities (Destinatario)
    @Query("SELECT e.destinatarioCiudad, COUNT(e) FROM Envio e GROUP BY e.destinatarioCiudad ORDER BY COUNT(e) DESC")
    List<Object[]> findTopCities();

    // Hourly Activity (Created At)
    @Query("SELECT HOUR(e.createdAt), COUNT(e) FROM Envio e GROUP BY HOUR(e.createdAt)")
    List<Object[]> findHourlyActivity();
}
