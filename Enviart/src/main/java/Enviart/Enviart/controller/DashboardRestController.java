package Enviart.Enviart.controller;

import Enviart.Enviart.dto.DashboardStatsDTO;
import Enviart.Enviart.repository.EnvioRepository;
import Enviart.Enviart.util.enums.EstadoEnvio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    @Autowired
    private EnvioRepository envioRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // 1. Total Revenue
        BigDecimal totalRevenue = envioRepository.sumTotalTarifa();
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 2. Monthly Revenue (Initialize with 0s)
        List<BigDecimal> monthlyRevenue = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));
        List<Object[]> monthlyData = envioRepository.findMonthlyRevenue();
        for (Object[] row : monthlyData) {
            int month = (int) row[0]; // 1-12
            BigDecimal sum = (BigDecimal) row[1];
            if (month >= 1 && month <= 12) {
                monthlyRevenue.set(month - 1, sum);
            }
        }
        stats.setMonthlyRevenue(monthlyRevenue);

        // 3. Shipment Status Counts
        Map<String, Long> statusCounts = new HashMap<>();
        List<Object[]> statusData = envioRepository.countByEstado();
        long totalShipments = 0;
        long onTimeShipments = 0; // Mock logic for now, assuming Delivered is "On Time"

        for (Object[] row : statusData) {
            EstadoEnvio estado = (EstadoEnvio) row[0];
            Long count = (Long) row[1];
            statusCounts.put(estado.name(), count);
            totalShipments += count;
            if (estado == EstadoEnvio.ENTREGADO) {
                onTimeShipments += count;
            }
        }
        stats.setShipmentStatusCounts(statusCounts);

        // 4. On-Time Delivery Rate
        double rate = totalShipments > 0 ? (double) onTimeShipments / totalShipments * 100 : 0;
        stats.setOnTimeDeliveryRate(Math.round(rate * 100.0) / 100.0);

        // 5. Top Cities
        Map<String, Long> topCities = new LinkedHashMap<>();
        List<Object[]> cityData = envioRepository.findTopCities();
        int limit = 5;
        for (Object[] row : cityData) {
            if (limit-- == 0)
                break;
            String city = (String) row[0];
            Long count = (Long) row[1];
            topCities.put(city, count);
        }
        stats.setTopCities(topCities);

        // 6. Hourly Activity
        List<Long> hourlyActivity = new ArrayList<>(Collections.nCopies(24, 0L));
        List<Object[]> hourlyData = envioRepository.findHourlyActivity();
        for (Object[] row : hourlyData) {
            int hour = (int) row[0]; // 0-23
            Long count = (Long) row[1];
            if (hour >= 0 && hour <= 23) {
                hourlyActivity.set(hour, count);
            }
        }
        stats.setHourlyActivity(hourlyActivity);

        return ResponseEntity.ok(stats);
    }
}
