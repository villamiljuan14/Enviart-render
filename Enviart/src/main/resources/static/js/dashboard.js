/* dashboard.js */

document.addEventListener('DOMContentLoaded', function () {
    initDarkMode();
    initCharts();
});

function initDarkMode() {
    // Check for saved user preference, if any, on load
    if (localStorage.getItem('color-theme') === 'dark' || (!('color-theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
        document.documentElement.classList.add('dark');
    } else {
        document.documentElement.classList.remove('dark');
    }

    // Find the toggle button (we'll add this ID to the HTML)
    var themeToggleBtn = document.getElementById('theme-toggle');

    if (themeToggleBtn) {
        var darkIcon = document.getElementById('theme-toggle-dark-icon');
        var lightIcon = document.getElementById('theme-toggle-light-icon');

        // Initial icon state
        if (document.documentElement.classList.contains('dark')) {
            darkIcon.classList.add('hidden');
            lightIcon.classList.remove('hidden');
        } else {
            lightIcon.classList.add('hidden');
            darkIcon.classList.remove('hidden');
        }

        themeToggleBtn.addEventListener('click', function () {
            // toggle icons
            darkIcon.classList.toggle('hidden');
            lightIcon.classList.toggle('hidden');

            // if set via local storage previously
            if (localStorage.getItem('color-theme')) {
                if (localStorage.getItem('color-theme') === 'light') {
                    document.documentElement.classList.add('dark');
                    localStorage.setItem('color-theme', 'dark');
                } else {
                    document.documentElement.classList.remove('dark');
                    localStorage.setItem('color-theme', 'light');
                }
            } else {
                // if NOT set via local storage previously
                if (document.documentElement.classList.contains('dark')) {
                    document.documentElement.classList.remove('dark');
                    localStorage.setItem('color-theme', 'light');
                } else {
                    document.documentElement.classList.add('dark');
                    localStorage.setItem('color-theme', 'dark');
                }
            }

            // Re-render charts to update colors if needed
            // This is a bit heavy, but necessary for ApexCharts to pick up new CSS variables or theme
            // For now, we'll just reload the page or rely on CSS overrides where possible.
            // ApexCharts has a updateOptions method which is better.
            updateChartsTheme();
        });
    }
}

function updateChartsTheme() {
    // Helper to update all charts on the page to the current theme
    const isDark = document.documentElement.classList.contains('dark');
    const themeMode = isDark ? 'dark' : 'light';

    // This assumes we store chart instances globally or can access them. 
    // Since we are moving to a module pattern, we might need to track instances.
    // For simplicity in this refactor, we will rely on CSS overrides for the tooltip 
    // and maybe reload if strictly necessary for grid lines. 
    // However, let's try to update if we have the instances.
    // We'll store instances in a global array for this purpose.
    if (window.apexChartsInstances) {
        window.apexChartsInstances.forEach(chart => {
            chart.updateOptions({
                theme: {
                    mode: themeMode
                },
                chart: {
                    background: 'transparent'
                },
                tooltip: {
                    theme: themeMode
                }
            });
        });
    }
}

window.apexChartsInstances = [];

function initCharts() {
    // Admin Dashboard Charts
    if (document.getElementById('revenueChart')) {
        initAdminCharts();
    }

    // Mensajero Dashboard Charts
    if (document.getElementById('weeklyChart')) {
        initMensajeroCharts();
    }

    // Proveedor Dashboard Charts
    if (document.getElementById('salesChart')) {
        initProveedorCharts();
    }
}

function initAdminCharts() {
    fetch('/api/dashboard/stats')
        .then(response => response.json())
        .then(data => {
            renderAdminCharts(data);
        })
        .catch(error => {
            console.error('Error loading dashboard stats:', error);
            // Fallback mock data if fetch fails (for development/demo)
            const mockData = {
                monthlyRevenue: [30000, 40000, 35000, 50000, 49000, 60000, 70000, 91000, 125000, 100000, 140000, 150000],
                shipmentStatusCounts: { 'Entregado': 65, 'En Tránsito': 25, 'Pendiente': 10 },
                onTimeDeliveryRate: 92,
                topCities: { 'Bogotá': 400, 'Medellín': 300, 'Cali': 200, 'Barranquilla': 100 },
                hourlyActivity: Array(24).fill(0).map(() => Math.floor(Math.random() * 100))
            };
            renderAdminCharts(mockData);
        });
}

function renderAdminCharts(data) {
    const commonOptions = {
        chart: { fontFamily: 'Outfit, sans-serif', background: 'transparent', toolbar: { show: false } },
        theme: { mode: document.documentElement.classList.contains('dark') ? 'dark' : 'light' }
    };

    // Revenue Chart
    var revenueOptions = {
        ...commonOptions,
        series: [{ name: 'Ingresos', data: data.monthlyRevenue }],
        chart: { ...commonOptions.chart, height: 320, type: 'area' },
        colors: ['#4f46e5'],
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 3 },
        fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.7, opacityTo: 0.1, stops: [0, 90, 100] } },
        xaxis: { categories: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'], labels: { style: { colors: '#64748b' } }, axisBorder: { show: false }, axisTicks: { show: false } },
        yaxis: { labels: { style: { colors: '#64748b' }, formatter: function (value) { return "$" + (value / 1000).toFixed(1) + "k"; } } },
        grid: { borderColor: '#f1f5f9', strokeDashArray: 4 }
    };
    const revenueChart = new ApexCharts(document.querySelector("#revenueChart"), revenueOptions);
    revenueChart.render();
    window.apexChartsInstances.push(revenueChart);

    // Status Chart
    var statusLabels = Object.keys(data.shipmentStatusCounts);
    var statusValues = Object.values(data.shipmentStatusCounts);
    var statusOptions = {
        ...commonOptions,
        series: statusValues,
        labels: statusLabels,
        chart: { ...commonOptions.chart, type: 'donut', height: 280 },
        colors: ['#6366f1', '#a855f7', '#eab308', '#22c55e', '#ef4444'],
        plotOptions: { pie: { donut: { size: '75%', labels: { show: true, total: { show: true, showAlways: true, label: 'Total', fontSize: '14px', color: '#64748b', formatter: function (w) { return w.globals.seriesTotals.reduce((a, b) => { return a + b }, 0); } } } } } },
        dataLabels: { enabled: false },
        legend: { show: true, position: 'bottom' },
        stroke: { show: false }
    };
    const statusChart = new ApexCharts(document.querySelector("#statusChart"), statusOptions);
    statusChart.render();
    window.apexChartsInstances.push(statusChart);

    // Radial Bar
    var radialOptions = {
        ...commonOptions,
        series: [data.onTimeDeliveryRate],
        chart: { ...commonOptions.chart, height: 320, type: 'radialBar' },
        plotOptions: { radialBar: { hollow: { size: '70%' }, dataLabels: { show: true, name: { offsetY: -10, show: true, color: '#64748b', fontSize: '17px' }, value: { offsetY: 5, color: '#1e293b', fontSize: '36px', show: true, fontWeight: 700, formatter: function (val) { return val + "%"; } } } } },
        labels: ['A Tiempo'],
        colors: ['#10b981'],
        stroke: { lineCap: 'round' }
    };
    const radialChart = new ApexCharts(document.querySelector("#radialChart"), radialOptions);
    radialChart.render();
    window.apexChartsInstances.push(radialChart);

    // Bar Chart
    var cityLabels = Object.keys(data.topCities);
    var cityValues = Object.values(data.topCities);
    var barOptions = {
        ...commonOptions,
        series: [{ name: 'Envíos', data: cityValues }],
        chart: { ...commonOptions.chart, type: 'bar', height: 320 },
        plotOptions: { bar: { borderRadius: 4, horizontal: true, barHeight: '50%' } },
        dataLabels: { enabled: false },
        xaxis: { categories: cityLabels, labels: { style: { colors: '#64748b' } } },
        yaxis: { labels: { style: { colors: '#64748b' } } },
        colors: ['#3b82f6'],
        grid: { borderColor: '#f1f5f9' }
    };
    const barChart = new ApexCharts(document.querySelector("#barChart"), barOptions);
    barChart.render();
    window.apexChartsInstances.push(barChart);

    // Heatmap
    var heatmapSeries = [
        { name: 'Lun', data: mapHourlyToHeatmap(data.hourlyActivity) },
        { name: 'Mar', data: mapHourlyToHeatmap(data.hourlyActivity) },
        { name: 'Mie', data: mapHourlyToHeatmap(data.hourlyActivity) },
        { name: 'Jue', data: mapHourlyToHeatmap(data.hourlyActivity) },
        { name: 'Vie', data: mapHourlyToHeatmap(data.hourlyActivity) }
    ];
    var heatmapOptions = {
        ...commonOptions,
        series: heatmapSeries,
        chart: { ...commonOptions.chart, height: 320, type: 'heatmap' },
        plotOptions: { heatmap: { shadeIntensity: 0.5, radius: 4, useFillColorAsStroke: true, colorScale: { ranges: [{ from: 0, to: 0, name: 'Sin Actividad', color: '#f1f5f9' }, { from: 1, to: 5, name: 'Bajo', color: '#93c5fd' }, { from: 6, to: 100, name: 'Alto', color: '#2563eb' }] } } },
        dataLabels: { enabled: false },
        stroke: { width: 1 },
        colors: ['#2563eb'],
        xaxis: { labels: { style: { colors: '#64748b' } } },
        yaxis: { labels: { style: { colors: '#64748b' } } }
    };
    const heatmapChart = new ApexCharts(document.querySelector("#heatmapChart"), heatmapOptions);
    heatmapChart.render();
    window.apexChartsInstances.push(heatmapChart);

    // Radar
    var radarOptions = {
        ...commonOptions,
        series: [{ name: 'Métricas', data: [80, 90, 70, 100, 85, 75] }],
        chart: { ...commonOptions.chart, height: 320, type: 'radar' },
        xaxis: { categories: ['Velocidad', 'Confiabilidad', 'Costo', 'Atención', 'Cobertura', 'Seguridad'], labels: { style: { colors: ['#64748b', '#64748b', '#64748b', '#64748b', '#64748b', '#64748b'], fontSize: '12px', fontFamily: 'Outfit, sans-serif' } } },
        stroke: { width: 2, colors: ['#8b5cf6'] },
        fill: { opacity: 0.2, colors: ['#8b5cf6'] },
        markers: { size: 4, colors: ['#fff'], strokeColors: '#8b5cf6', strokeWidth: 2 },
        yaxis: { show: false }
    };
    const radarChart = new ApexCharts(document.querySelector("#radarChart"), radarOptions);
    radarChart.render();
    window.apexChartsInstances.push(radarChart);
}

function mapHourlyToHeatmap(hourlyData) {
    return hourlyData.map((count, hour) => {
        return {
            x: hour.toString().padStart(2, '0') + ':00',
            y: count
        };
    });
}

function initMensajeroCharts() {
    const commonOptions = {
        chart: { fontFamily: 'Outfit, sans-serif', background: 'transparent', toolbar: { show: false } },
        theme: { mode: document.documentElement.classList.contains('dark') ? 'dark' : 'light' }
    };

    // Weekly Performance
    var weeklyOptions = {
        ...commonOptions,
        series: [{ name: 'Entregas', data: [18, 22, 19, 25, 21, 23, 15] }],
        chart: { ...commonOptions.chart, height: 256, type: 'line' },
        colors: ['#0ea5e9'],
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 3 },
        xaxis: { categories: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'], labels: { style: { colors: '#64748b', fontSize: '11px' } } },
        yaxis: { labels: { style: { colors: '#64748b' } } },
        grid: { borderColor: '#f1f5f9', strokeDashArray: 4 }
    };
    const weeklyChart = new ApexCharts(document.querySelector("#weeklyChart"), weeklyOptions);
    weeklyChart.render();
    window.apexChartsInstances.push(weeklyChart);

    // Daily Deliveries
    var dailyOptions = {
        ...commonOptions,
        series: [{ name: 'Entregas', data: [18, 22, 19, 25, 21, 23, 15] }],
        chart: { ...commonOptions.chart, height: 320, type: 'bar' },
        plotOptions: { bar: { borderRadius: 8, columnWidth: '60%', distributed: true } },
        dataLabels: { enabled: false },
        colors: ['#06b6d4', '#0ea5e9', '#14b8a6', '#10b981', '#22c55e', '#84cc16', '#eab308'],
        xaxis: { categories: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'], labels: { style: { colors: '#64748b' } } },
        yaxis: { labels: { style: { colors: '#64748b' } } },
        grid: { borderColor: '#f1f5f9' },
        legend: { show: false }
    };
    const dailyChart = new ApexCharts(document.querySelector("#dailyDeliveriesChart"), dailyOptions);
    dailyChart.render();
    window.apexChartsInstances.push(dailyChart);

    // Earnings
    var earningsOptions = {
        ...commonOptions,
        series: [{ name: 'Ganancias', data: [850, 1200, 980, 1450, 1320, 1687] }],
        chart: { ...commonOptions.chart, height: 320, type: 'area' },
        colors: ['#10b981'],
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 3 },
        fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.7, opacityTo: 0.1, stops: [0, 90, 100] } },
        xaxis: { categories: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'], labels: { style: { colors: '#64748b' } } },
        yaxis: { labels: { style: { colors: '#64748b' }, formatter: function (value) { return "$" + value; } } },
        grid: { borderColor: '#f1f5f9', strokeDashArray: 4 }
    };
    const earningsChart = new ApexCharts(document.querySelector("#earningsChart"), earningsOptions);
    earningsChart.render();
    window.apexChartsInstances.push(earningsChart);
}

function initProveedorCharts() {
    const commonOptions = {
        chart: { fontFamily: 'Outfit, sans-serif', background: 'transparent', toolbar: { show: false } },
        theme: { mode: document.documentElement.classList.contains('dark') ? 'dark' : 'light' }
    };

    // Sales Chart
    var salesOptions = {
        ...commonOptions,
        series: [{ name: 'Ventas', data: [28000, 35000, 42000, 38000, 45200, 52000] }],
        chart: { ...commonOptions.chart, height: 320, type: 'area' },
        colors: ['#7c3aed'],
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 3 },
        fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.7, opacityTo: 0.1, stops: [0, 90, 100] } },
        xaxis: { categories: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'], labels: { style: { colors: '#64748b' } } },
        yaxis: { labels: { style: { colors: '#64748b' }, formatter: function (value) { return "$" + (value / 1000).toFixed(0) + "k"; } } },
        grid: { borderColor: '#f1f5f9', strokeDashArray: 4 }
    };
    const salesChart = new ApexCharts(document.querySelector("#salesChart"), salesOptions);
    salesChart.render();
    window.apexChartsInstances.push(salesChart);

    // Top Products Chart
    var topProductsOptions = {
        ...commonOptions,
        series: [45, 28, 18, 9],
        labels: ['Laptops', 'Smartphones', 'Tablets', 'Accesorios'],
        chart: { ...commonOptions.chart, type: 'donut', height: 320 },
        colors: ['#7c3aed', '#ec4899', '#f59e0b', '#10b981'],
        plotOptions: { pie: { donut: { size: '70%', labels: { show: true, total: { show: true, label: 'Total', fontSize: '14px', color: '#64748b', formatter: function (w) { return w.globals.seriesTotals.reduce((a, b) => a + b, 0) + '%'; } } } } } },
        dataLabels: { enabled: false },
        legend: { position: 'bottom', fontSize: '12px' },
        stroke: { show: false }
    };
    const topProductsChart = new ApexCharts(document.querySelector("#topProductsChart"), topProductsOptions);
    topProductsChart.render();
    window.apexChartsInstances.push(topProductsChart);
}
