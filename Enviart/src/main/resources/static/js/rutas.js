// Inicializar DataTables cuando el DOM esté listo
$(document).ready(function () {
    $('#rutasTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json'
        },
        order: [[0, 'desc']], // Ordenar por ID descendente
        pageLength: 10,
        responsive: true,
        columnDefs: [
            { orderable: false, targets: -1 } // Deshabilitar ordenamiento en columna de acciones
        ]
    });

    // Auto-ocultar mensajes de éxito/error después de 5 segundos
    setTimeout(function () {
        $('.bg-green-100, .bg-red-100').fadeOut('slow');
    }, 5000);
});
