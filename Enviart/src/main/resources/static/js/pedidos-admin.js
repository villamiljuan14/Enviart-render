document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById('pedidosTable')) {
        new DataTable('#pedidosTable', {
            responsive: true,
            language: {
                search: "Buscar:",
                lengthMenu: "Mostrar _MENU_ registros",
                info: "Mostrando _START_ a _END_ de _TOTAL_ pedidos",
                paginate: { previous: "Anterior", next: "Siguiente" },
                emptyTable: "No hay pedidos disponibles",
                infoEmpty: "Mostrando 0 de 0 pedidos",
                infoFiltered: "(filtrado de _MAX_ pedidos totales)"
            },
            columnDefs: [
                { targets: 7, orderable: false }
            ]
        });
    }
});
