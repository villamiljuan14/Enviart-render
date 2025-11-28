<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Reporte de Usuarios - Enviart</title>
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
            margin-top: 5px; /* Ajuste para dejar espacio al logo */
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
            padding-top: 10px;
        }
        /* ESTILO PARA EL LOGO CORPORATIVO */
        .header img.logo {
            max-width: 150px; /* Tamaño de tu logo, ajusta si es necesario */
            height: auto;
            margin-bottom: 15px;
            display: block; 
            margin-left: auto;
            margin-right: auto;
        }
        /* FIN ESTILO LOGO */

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
        
        /* ESTILOS PARA LA SECCIÓN DE MÉTRICAS CLAVE (RESUMEN EJECUTIVO) */
        .summary {
            display: flex; 
            justify-content: space-around;
            gap: 15px;
            margin: 25px 0;
        }
        .metric-box {
            flex: 1; 
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            text-align: center;
            background-color: #ffffff;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        .metric-box h3 {
            margin-top: 0;
            color: #3498db;
            font-size: 10px;
            margin-bottom: 5px;
        }
        .metric-box .value {
            font-size: 18px;
            font-weight: bold;
            color: #2c3e50;
        }
        /* FIN DE ESTILOS DE MÉTRICAS */

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
        .badge-ADMINISTRADOR { background-color: #dc2626; } 
        .badge-PROVEEDOR { background-color: #2563eb; } 
        .badge-MENSAJERO { background-color: #ca8a04; } 
        .badge-CLIENTE { background-color: #4b5563; } 
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
        <!-- Logo estilizado con texto para compatibilidad con PDF -->
        <div style="text-align: center; margin-bottom: 15px; padding: 10px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px;">
            <h2 style="color: white; font-size: 28px; margin: 0; letter-spacing: 3px; font-weight: bold;">ENVIART</h2>
            <p style="color: #f0f0f0; font-size: 10px; margin: 5px 0; letter-spacing: 1px;">SISTEMA DE GESTIÓN DE ENVÍOS</p>
        </div>
        
        <h1>REPORTE DE USUARIOS</h1>
        <div class="info">Generado el: ${fechaGeneracion}</div>
        <div class="info">Total de usuarios en el sistema: ${total}</div>
    </div>

    <#if desde?? || hasta??>
    <div class="filtros">
        <strong>Filtros de Registro Aplicados:</strong>
        <#if desde??>Desde: **${desde}**</#if>
        <#if hasta??> | Hasta: **${hasta}**</#if>
    </div>
    </#if>

    <div class="summary">
        <#if totalPeriodo??>
        <div class="metric-box">
            <h3>USUARIOS REGISTRADOS EN EL PERIODO</h3>
            <div class="value">${totalPeriodo}</div>
        </div>
        </#if>
        
        <#if porcentajeCliente??>
        <div class="metric-box">
            <h3>% CLIENTES</h3>
            <div class="value">${porcentajeCliente}%</div>
        </div>
        </#if>
        
        <#if porcentajeMensajero??>
        <div class="metric-box">
            <h3>% MENSAJEROS</h3>
            <div class="value">${porcentajeMensajero}%</div>
        </div>
        </#if>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre Completo</th>
                <th>Email</th>
                <th>Teléfono</th>
                <th>Documento</th>
                <th>Rol</th>
                <th>Fecha Registro</th>
            </tr>
        </thead>
        <tbody>
            <#list usuarios as usuario>
            <tr>
                <td>${usuario.idUsuario}</td>
                <td>
                    ${usuario.primerNombre} 
                    <#if usuario.segundoNombre??>${usuario.segundoNombre}</#if>
                    ${usuario.primerApellido}
                    <#if usuario.segundoApellido??>${usuario.segundoApellido}</#if>
                </td>
                <td>${usuario.email}</td>
                <td>${usuario.telefono}</td>
                <td>${usuario.tipoDocumento}: ${usuario.docUsuario}</td>
                <td>
                    <span class="badge badge-${usuario.rol.tipoRol}">
                        ${usuario.rol.tipoRol}
                    </span>
                </td>
                
                <td>${(usuario.createdAt?datetime("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))?string["dd/MM/yyyy HH:mm"]}</td>
                
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