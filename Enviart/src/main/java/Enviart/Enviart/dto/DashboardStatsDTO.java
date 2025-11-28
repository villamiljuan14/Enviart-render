package Enviart.Enviart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private BigDecimal totalRevenue;
    private List<BigDecimal> monthlyRevenue; // Index 0 = Jan, 11 = Dec
    private Map<String, Long> shipmentStatusCounts;
    private Map<String, Long> topCities;
    private Double onTimeDeliveryRate;
    private List<Long> hourlyActivity; // Index 0 = 00:00, 23 = 23:00
}
