document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById('tiposTable')) {
        new DataTable('#tiposTable', {
            responsive: true,
            language: {
                search: "Buscar:",
                lengthMenu: "Mostrar _MENU_ registros",
                info: "Mostrando _START_ a _END_ de _TOTAL_ tipos",
                paginate: { previous: "Anterior", next: "Siguiente" },
                emptyTable: "No hay tipos de servicio disponibles",
                infoEmpty: "Mostrando 0 de 0 tipos",
                infoFiltered: "(filtrado de _MAX_ tipos totales)"
            },
            columnDefs: [
                { targets: 5, orderable: false }
            ]
        });
    }
});
