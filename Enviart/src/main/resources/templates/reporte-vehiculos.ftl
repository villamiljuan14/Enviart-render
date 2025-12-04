<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Reporte de Vehículos - Enviart</title>
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
        
        <h1>REPORTE DE VEHÍCULOS</h1>
        <div class="info">Generado el: ${fechaGeneracion}</div>
        <div class="info">Total de vehículos en el sistema: ${total}</div>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Placa</th>
                <th>Modelo</th>
                <th>Cap. Volumen (m³)</th>
                <th>Cap. Peso (kg)</th>
            </tr>
        </thead>
        <tbody>
            <#list vehiculos as v>
            <tr>
                <td>${v.idVehiculo}</td>
                <td>${(v.placa)!"N/A"}</td>
                <td>${(v.modelo)!"N/A"}</td>
                <td>${(v.capacidadVolumen)!0}</td>
                <td>${(v.capacidadPeso)!0}</td>
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
