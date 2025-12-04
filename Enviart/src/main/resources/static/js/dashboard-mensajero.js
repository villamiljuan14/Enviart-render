document.addEventListener('DOMContentLoaded', function () {

    // ==========================================
    // 1. MOCK DATA
    // ==========================================
    const MOCK_DATA = {
        tasaExito: 92,
        weeklyDeliveries: [10, 15, 12, 18, 20, 25, 22]
    };

    // Detect dark mode
    const isDarkMode = () => document.documentElement.classList.contains('dark');

    // ==========================================
    // 2. APEXCHARTS INITIALIZATION
    // ==========================================

    // Chart 1: Success Rate (Radial Bar)
    const renderSuccessRateChart = (data) => {
        const options = {
            series: [data.tasaExito],
            chart: {
                height: 300,
                type: 'radialBar',
                toolbar: { show: false },
                background: 'transparent'
            },
            plotOptions: {
                radialBar: {
                    startAngle: -135,
                    endAngle: 225,
                    hollow: {
                        margin: 0,
                        size: '70%',
                        background: 'transparent',
                        dropShadow: {
                            enabled: true,
                            top: 3,
                            left: 0,
                            blur: 4,
                            opacity: 0.24
                        }
                    },
                    dataLabels: {
                        show: true,
                        name: {
                            offsetY: -10,
                            show: true,
                            color: isDarkMode() ? '#94a3b8' : '#64748b',
                            fontSize: '13px'
                        },
                        value: {
                            formatter: function (val) { return val + "%"; },
                            color: isDarkMode() ? '#ffffff' : '#1e293b',
                            fontSize: '30px',
                            show: true,
                        }
                    }
                }
            },
            fill: {
                type: 'gradient',
                gradient: {
                    shade: 'dark',
                    type: 'horizontal',
                    shadeIntensity: 0.5,
                    gradientToColors: ['#3b82f6'],
                    inverseColors: false,
                    opacityFrom: 1,
                    opacityTo: 1,
                    stops: [0, 100]
                }
            },
            stroke: { lineCap: 'round' },
            labels: ['Éxito'],
        };

        const chartElement = document.querySelector("#deliverySuccessChart");
        if (chartElement) {
            chartElement.innerHTML = ''; // Clear previous
            const chart = new ApexCharts(chartElement, options);
            chart.render();
            return chart;
        }
    };

    // Chart 2: Weekly Deliveries (Area)
    const renderWeeklyDeliveriesChart = (data) => {
        const options = {
            series: [{
                name: 'Entregas',
                data: data.weeklyDeliveries
            }],
            chart: {
                height: 350,
                type: 'area',
                toolbar: { show: false },
                zoom: { enabled: false },
                foreColor: isDarkMode() ? '#94a3b8' : '#64748b',
                background: 'transparent'
            },
            dataLabels: { enabled: false },
            stroke: {
                curve: 'smooth',
                width: 3,
                colors: ['#3b82f6']
            },
            xaxis: {
                categories: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
                axisBorder: { show: false },
                axisTicks: { show: false },
            },
            yaxis: {
                labels: {
                    formatter: function (val) { return val.toFixed(0); }
                }
            },
            grid: {
                borderColor: isDarkMode() ? '#334155' : '#e2e8f0',
                strokeDashArray: 4,
            },
            fill: {
                type: 'gradient',
                gradient: {
                    shade: 'light',
                    type: 'vertical',
                    shadeIntensity: 0.5,
                    opacityFrom: 0.7,
                    opacityTo: 0.3,
                    stops: [0, 100],
                    colorStops: [{
                        offset: 0,
                        color: '#3b82f6',
                        opacity: 1
                    }, {
                        offset: 100,
                        color: '#3b82f6',
                        opacity: 0.1
                    }],
                }
            },
            tooltip: {
                theme: isDarkMode() ? 'dark' : 'light',
            },
            colors: ['#3b82f6']
        };

        const chartElement = document.querySelector("#weeklyDeliveriesChart");
        if (chartElement) {
            chartElement.innerHTML = ''; // Clear previous
            const chart = new ApexCharts(chartElement, options);
            chart.render();
            return chart;
        }
    };

    // Initial Render
    let successChart = renderSuccessRateChart(MOCK_DATA);
    let weeklyChart = renderWeeklyDeliveriesChart(MOCK_DATA);

    // Theme Toggle Listener
    const themeToggle = document.getElementById('theme-toggle');
    if (themeToggle) {
        themeToggle.addEventListener('click', function () {
            document.body.classList.toggle('dark');
            // Re-render charts
            setTimeout(() => {
                successChart = renderSuccessRateChart(MOCK_DATA);
                weeklyChart = renderWeeklyDeliveriesChart(MOCK_DATA);

                // Update Map Tiles if needed (optional, Leaflet handles this via CSS mostly or different tile providers)
            }, 100);
        });
    }

    // ==========================================
    // 3. LEAFLET MAP IMPLEMENTATION (ROUTE)
    // ==========================================
    const mapContainer = document.getElementById('mapaRutaMensajero');
    if (mapContainer && typeof L !== 'undefined') {
        // Clear placeholder text
        mapContainer.innerHTML = '';

        // Default to Bogotá
        const defaultLat = 4.6500;
        const defaultLng = -74.0500;

        const map = L.map('mapaRutaMensajero').setView([defaultLat, defaultLng], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        // Route Points (Mock)
        const routePoints = [
            [4.6480, -74.0550], // Start
            [4.6520, -74.0520], // Waypoint
            [4.6550, -74.0480]  // End
        ];

        // Draw Polyline
        const polyline = L.polyline(routePoints, { color: 'blue' }).addTo(map);

        // Add Markers
        L.marker(routePoints[0]).addTo(map).bindPopup('Inicio de Ruta');
        L.marker(routePoints[2]).addTo(map).bindPopup('Destino Actual');

        // Fit bounds
        map.fitBounds(polyline.getBounds());
    }
});
