$(document).ready(function () {
    $('#tiposPagoTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json'
        },
        order: [[0, 'asc']],
        pageLength: 10,
        responsive: true,
        columnDefs: [
            { orderable: false, targets: -1 }
        ]
    });

    setTimeout(function () {
        $('.bg-green-100, .bg-red-100').fadeOut('slow');
    }, 5000);
});
