document.addEventListener('DOMContentLoaded', () => {

    // ==========================================
    // 1. CHART.JS INITIALIZATION
    // ==========================================
    const ctx = document.getElementById('proveedor-chart');
    if (ctx) {
        const primaryColor = getComputedStyle(document.documentElement).getPropertyValue('--primary').trim() || '#4f46e5';
        const primaryHoverColor = '#4338ca';

        const chartData = {
            labels: ['Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
            datasets: [{
                label: 'Pedidos Recibidos',
                data: [350, 410, 380, 400, 395, 420, 480], // Mock Data
                backgroundColor: primaryColor,
                borderColor: primaryColor,
                borderWidth: 1,
                borderRadius: 4,
                hoverBackgroundColor: primaryHoverColor,
            }]
        };

        const chartConfig = {
            type: 'bar',
            data: chartData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(200, 200, 200, 0.2)',
                            border: false
                        },
                        ticks: {
                            color: getComputedStyle(document.body).getPropertyValue('color')
                        }
                    },
                    x: {
                        grid: {
                            display: false,
                        },
                        ticks: {
                            color: getComputedStyle(document.body).getPropertyValue('color')
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        titleColor: '#fff',
                        bodyColor: '#fff',
                        borderColor: primaryColor,
                        borderWidth: 1,
                    }
                }
            }
        };

        new Chart(ctx.getContext('2d'), chartConfig);
    }

    // ==========================================
    // 2. LEAFLET MAP IMPLEMENTATION (COVERAGE)
    // ==========================================
    const mapContainer = document.getElementById('coverage-map');
    if (mapContainer && typeof L !== 'undefined') {
        // Default to Bogotá
        const defaultLat = 4.6097;
        const defaultLng = -74.0817;

        const map = L.map('coverage-map').setView([defaultLat, defaultLng], 12);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        // Add some "coverage" circles (Mock)
        const zones = [
            { lat: 4.6097, lng: -74.0817, radius: 2000, color: 'blue' }, // Central
            { lat: 4.6500, lng: -74.0500, radius: 1500, color: 'green' }, // North
            { lat: 4.5800, lng: -74.1000, radius: 1800, color: 'red' }    // South
        ];

        zones.forEach(zone => {
            L.circle([zone.lat, zone.lng], {
                color: zone.color,
                fillColor: zone.color,
                fillOpacity: 0.3,
                radius: zone.radius
            }).addTo(map).bindPopup('Zona de Cobertura Activa');
        });
    }
});
