// Dark Mode Logic
const themeToggleBtn = document.getElementById('theme-toggle');
const darkIcon = document.getElementById('theme-toggle-dark-icon');
const lightIcon = document.getElementById('theme-toggle-light-icon');

// Check local storage or system preference
if (localStorage.getItem('color-theme') === 'dark' || (!('color-theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    document.documentElement.classList.add('dark');
    lightIcon.classList.remove('hidden');
} else {
    document.documentElement.classList.remove('dark');
    darkIcon.classList.remove('hidden');
}

themeToggleBtn.addEventListener('click', function () {
    darkIcon.classList.toggle('hidden');
    lightIcon.classList.toggle('hidden');

    if (localStorage.getItem('color-theme')) {
        if (localStorage.getItem('color-theme') === 'light') {
            document.documentElement.classList.add('dark');
            localStorage.setItem('color-theme', 'dark');
        } else {
            document.documentElement.classList.remove('dark');
            localStorage.setItem('color-theme', 'light');
        }
    } else {
        if (document.documentElement.classList.contains('dark')) {
            document.documentElement.classList.remove('dark');
            localStorage.setItem('color-theme', 'light');
        } else {
            document.documentElement.classList.add('dark');
            localStorage.setItem('color-theme', 'dark');
        }
    }
});

// ApexCharts Configuration
const options = {
    series: [{
        name: 'Envíos',
        data: [44, 55, 57, 56, 61, 58, 63, 60, 66]
    }, {
        name: 'Entregas',
        data: [76, 85, 101, 98, 87, 105, 91, 114, 94]
    }],
    chart: {
        type: 'bar',
        height: 320,
        toolbar: { show: false },
        fontFamily: 'Outfit, sans-serif',
        background: 'transparent'
    },
    colors: ['#3C50E0', '#80CAEE'],
    plotOptions: {
        bar: {
            horizontal: false,
            columnWidth: '55%',
            endingShape: 'rounded',
            borderRadius: 4
        },
    },
    dataLabels: { enabled: false },
    stroke: {
        show: true,
        width: 2,
        colors: ['transparent']
    },
    xaxis: {
        categories: ['Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct'],
        axisBorder: { show: false },
        axisTicks: { show: false },
        labels: {
            style: { colors: '#64748B' }
        }
    },
    yaxis: {
        labels: {
            style: { colors: '#64748B' }
        }
    },
    fill: { opacity: 1 },
    tooltip: {
        y: {
            formatter: function (val) {
                return val + " envíos"
            }
        },
        theme: 'dark'
    },
    grid: {
        borderColor: '#e2e8f0',
        strokeDashArray: 4,
        yaxis: { lines: { show: true } }
    },
    legend: {
        position: 'top',
        horizontalAlign: 'left',
        labels: { colors: '#64748B' }
    }
};

// Adjust chart for dark mode dynamically
const updateChartTheme = () => {
    if (document.documentElement.classList.contains('dark')) {
        options.grid.borderColor = '#374151';
        options.xaxis.labels.style.colors = '#9CA3AF';
        options.yaxis.labels.style.colors = '#9CA3AF';
        options.legend.labels.colors = '#9CA3AF';
    } else {
        options.grid.borderColor = '#e2e8f0';
        options.xaxis.labels.style.colors = '#64748B';
        options.yaxis.labels.style.colors = '#64748B';
        options.legend.labels.colors = '#64748B';
    }
    if (chart) {
        chart.updateOptions(options);
    }
};

const chart = new ApexCharts(document.querySelector("#deliveryChart"), options);
chart.render();

// Listen for theme toggle to update chart
themeToggleBtn.addEventListener('click', () => {
    setTimeout(updateChartTheme, 50);
});

// Initial check
updateChartTheme();
