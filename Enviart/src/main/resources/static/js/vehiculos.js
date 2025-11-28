document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById('vehiculosTable')) {
        new DataTable('#vehiculosTable', {
            responsive: true,
            language: {
                search: "Buscar:",
                lengthMenu: "Mostrar _MENU_ registros",
                info: "Mostrando _START_ a _END_ de _TOTAL_ vehículos",
                paginate: { previous: "Anterior", next: "Siguiente" },
                emptyTable: "No hay vehículos disponibles",
                infoEmpty: "Mostrando 0 de 0 vehículos",
                infoFiltered: "(filtrado de _MAX_ vehículos totales)"
            },
            columnDefs: [
                { targets: 5, orderable: false }
            ]
        });
    }
});
