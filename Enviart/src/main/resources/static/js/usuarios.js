// Script para inicializar DataTable
document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById('usuariosTable')) {
        // Inicialización de DataTables (requiere jQuery)
        new DataTable('#usuariosTable', {
            responsive: true,
            language: {
                search: "Buscar:",
                lengthMenu: "Mostrar _MENU_ registros",
                info: "Mostrando _START_ a _END_ de _TOTAL_ usuarios",
                paginate: { previous: "Anterior", next: "Siguiente" },
                emptyTable: "No hay usuarios disponibles",
                infoEmpty: "Mostrando 0 de 0 usuarios",
                infoFiltered: "(filtrado de _MAX_ usuarios totales)"
            },
            // Deshabilitar ordenamiento en la columna de Acciones (índice 7)
            columnDefs: [
                { targets: 7, orderable: false }
            ]
        });
    }
});
