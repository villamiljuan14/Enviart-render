document.addEventListener('DOMContentLoaded', () => {

    // ==========================================
    // 1. CHART LOGIC
    // ==========================================
    const chartDataContainer = document.getElementById('chart-data-container');
    const chartDataJsonString = chartDataContainer ? chartDataContainer.getAttribute('data-chart') : null;

    // Default data
    let chartData = {
        series: [60, 15, 5, 10],
        categories: ['Entregado', 'En Tránsito', 'Con Novedad', 'Pendiente'],
        colors: ['#10B981', '#3B82F6', '#EF4444', '#FCD34D']
    };

    try {
        if (chartDataJsonString) {
            chartData = JSON.parse(chartDataJsonString);
        }
    } catch (e) {
        console.error("Error parsing chartDataJson:", e);
    }

    // ==========================================
    // 2. THEME & DARK MODE LOGIC
    // ==========================================
    const themeToggle = document.getElementById('theme-toggle');
    const sunIcon = document.getElementById('sun-icon');
    const moonIcon = document.getElementById('moon-icon');
    let chart = null;

    const updateChartColors = (isDarkMode) => {
        const textColor = isDarkMode ? '#E2E8F0' : '#333';
        const labelColor = isDarkMode ? '#94A3B8' : '#333';

        if (chart) {
            chart.updateOptions({
                chart: {
                    foreColor: textColor,
                },
                xaxis: {
                    labels: { style: { colors: labelColor } }
                },
                yaxis: {
                    title: { style: { color: textColor } },
                    labels: { style: { colors: labelColor } }
                }
            });
        }
    }

    const toggleTheme = () => {
        const isDarkMode = document.documentElement.classList.toggle('dark');
        localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
        updateIcon(isDarkMode);
        updateChartColors(isDarkMode);
    };

    const updateIcon = (isDarkMode) => {
        if (sunIcon && moonIcon) {
            if (isDarkMode) {
                moonIcon.classList.add('hidden');
                sunIcon.classList.remove('hidden');
            } else {
                sunIcon.classList.add('hidden');
                moonIcon.classList.remove('hidden');
            }
        }
    };

    // Initialize theme
    const currentTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    let initialDarkMode = false;

    if (currentTheme === 'dark' || (!currentTheme && prefersDark)) {
        document.documentElement.classList.add('dark');
        initialDarkMode = true;
    }
    updateIcon(initialDarkMode);

    // ==========================================
    // 3. APEXCHARTS INITIALIZATION
    // ==========================================
    if (document.querySelector("#envios-chart")) {
        var chartOptions = {
            series: [{
                name: "Total de Envíos",
                data: chartData.series
            }],
            chart: {
                type: 'bar',
                height: 350,
                toolbar: { show: false },
                foreColor: initialDarkMode ? '#E2E8F0' : '#333'
            },
            plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: '60%',
                    borderRadius: 4
                },
            },
            dataLabels: {
                enabled: false
            },
            xaxis: {
                categories: chartData.categories,
                labels: {
                    style: {
                        colors: initialDarkMode ? '#94A3B8' : '#333'
                    }
                }
            },
            yaxis: {
                title: {
                    text: 'Cantidad de Pedidos',
                    style: {
                        color: initialDarkMode ? '#E2E8F0' : '#333'
                    }
                },
                labels: {
                    style: {
                        colors: initialDarkMode ? '#94A3B8' : '#333'
                    }
                }
            },
            fill: {
                opacity: 1
            },
            tooltip: {
                y: {
                    formatter: function (val) {
                        return val + " envíos"
                    }
                }
            },
            colors: chartData.colors
        };

        chart = new ApexCharts(document.querySelector("#envios-chart"), chartOptions);
        chart.render();
    }

    if (themeToggle) {
        themeToggle.addEventListener('click', toggleTheme);
    }

    // ==========================================
    // 4. LEAFLET MAP IMPLEMENTATION (TRACKING)
    // ==========================================
    const mapContainer = document.getElementById('tracking-map');
    if (mapContainer && typeof L !== 'undefined') {
        // Default to Bogotá
        const defaultLat = 4.6097;
        const defaultLng = -74.0817;

        const map = L.map('tracking-map').setView([defaultLat, defaultLng], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        // Add a marker for the "current" location (Mock)
        const marker = L.marker([defaultLat, defaultLng]).addTo(map)
            .bindPopup('<b>Tu Pedido</b><br>En tránsito hacia destino.')
            .openPopup();

        // Mock movement
        // In a real app, this would connect to WebSocket or poll an API
    }
});
