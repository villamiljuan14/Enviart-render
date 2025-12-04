<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Reporte de Rutas - Enviart</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            font-size: 12px;
        }
        h1 {
            color: #2c3e50;
            border-bottom: 3px solid #3498db;
            padding-bottom: 10px;
            text-align: center;
            margin-top: 5px;
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
            padding-top: 10px;
        }
        .info {
            margin-bottom: 10px;
            color: #7f8c8d;
        }
        .filtros {
            background-color: #ecf0f1;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            font-size: 11px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th {
            background-color: #3498db;
            color: white;
            padding: 10px;
            text-align: left;
            font-size: 10px;
        }
        td {
            padding: 8px;
            border-bottom: 1px solid #ddd;
            font-size: 9px;
        }
        tr:nth-child(even) {
            background-color: #f8f9fa;
        }
        .badge {
            padding: 3px 6px;
            border-radius: 3px;
            font-weight: bold;
            font-size: 8px;
            color: white;
        }
        .badge-PLANIFICADA { background-color: #ca8a04; }
        .badge-EN_CURSO { background-color: #2563eb; }
        .badge-COMPLETADA { background-color: #16a34a; }
        .badge-CANCELADA { background-color: #dc2626; }
        .footer {
            margin-top: 40px;
            text-align: center;
            color: #7f8c8d;
            font-size: 10px;
        }
    </style>
</head>
<body>
    <div class="header">
        <div style="text-align: center; margin-bottom: 15px; padding: 10px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px;">
            <h2 style="color: white; font-size: 28px; margin: 0; letter-spacing: 3px; font-weight: bold;">ENVIART</h2>
            <p style="color: #f0f0f0; font-size: 10px; margin: 5px 0; letter-spacing: 1px;">SISTEMA DE GESTIÓN DE ENVÍOS</p>
        </div>
        
        <h1>REPORTE DE RUTAS</h1>
        <div class="info">Generado el: ${fechaGeneracion}</div>
        <div class="info">Total de rutas en el sistema: ${total}</div>
    </div>

    <#if desde??>
    <div class="filtros">
        <strong>Filtros Aplicados:</strong>
        Fecha Inicio: **${desde}**
    </div>
    </#if>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre Ruta</th>
                <th>Vehículo</th>
                <th>Conductor</th>
                <th>Status</th>
                <th>Fecha Inicio</th>
            </tr>
        </thead>
        <tbody>
            <#list rutas as r>
            <tr>
                <td>${r.idRuta}</td>
                <td>${r.nombreRuta}</td>
                <td>${r.vehiculoPlaca}</td>
                <td>${r.conductorNombre}</td>
                <td>
                    <span class="badge badge-${r.statusRuta}">
                        ${r.statusRuta}
                    </span>
                </td>
                <td>${r.fechaInicio}</td>
            </tr>
            </#list>
        </tbody>
    </table>

    <div class="footer">
        <p>Enviart - Sistema de Gestión de Envíos</p>
        <p>Este documento fue generado automáticamente. Contiene información confidencial.</p>
    </div>
</body>
</html>
