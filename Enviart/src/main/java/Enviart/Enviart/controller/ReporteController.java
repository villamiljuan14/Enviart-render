package Enviart.Enviart.controller;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.optimizada.Pedido;
import Enviart.Enviart.model.optimizada.Ruta;
import Enviart.Enviart.model.optimizada.Vehiculo;
import Enviart.Enviart.service.UsuarioService;
import Enviart.Enviart.service.PedidoService;
import Enviart.Enviart.service.RutaService;
import Enviart.Enviart.service.VehiculoService;
import Enviart.Enviart.util.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para generación de reportes de usuarios
 */
@Controller
public class ReporteController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final VehiculoService vehiculoService;
    private final RutaService rutaService;
    private final PdfGenerator pdfGenerator;

    public ReporteController(UsuarioService usuarioService, PedidoService pedidoService,
            VehiculoService vehiculoService,
            RutaService rutaService, PdfGenerator pdfGenerator) {
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.vehiculoService = vehiculoService;
        this.rutaService = rutaService;
        this.pdfGenerator = pdfGenerator;
    }

    /**
     * Genera y descarga el reporte PDF de usuarios
     * GET /reporte-usuarios
     */
    @GetMapping("/reporte-usuarios")
    public void reporteUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String desdeStr,
            @RequestParam(required = false) String hastaStr,
            @RequestParam(required = false) String rol,
            HttpServletResponse response) throws Exception {

        // Tratar cadenas vacías como null para los filtros
        String processedNombre = (nombre != null && nombre.isEmpty()) ? null : nombre;
        String processedRol = (rol != null && rol.isEmpty()) ? null : rol;

        // Parsear fechas de String a LocalDate de forma segura
        LocalDate desde = null;
        if (desdeStr != null && !desdeStr.isEmpty()) {
            desde = LocalDate.parse(desdeStr);
        }
        LocalDate hasta = null;
        if (hastaStr != null && !hastaStr.isEmpty()) {
            hasta = LocalDate.parse(hastaStr);
        }

        // Obtener usuarios filtrados
        List<Usuario> usuarios = usuarioService.filtrarUsuarios(processedNombre, desde, hasta, processedRol);

        // Generar PDF
        pdfGenerator.generarPdf("reporte-usuarios", "usuarios", usuarios, desde, hasta, response);
    }

    /**
     * Genera y descarga el reporte PDF de pedidos
     * GET /reporte-pedidos
     */
    @GetMapping("/reporte-pedidos")
    public void reportePedidos(
            @RequestParam(required = false) String desdeStr,
            @RequestParam(required = false) String hastaStr,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String usuarioEmail,
            HttpServletResponse response) throws Exception {

        // Parsear fechas
        LocalDate desde = null;
        if (desdeStr != null && !desdeStr.isEmpty()) {
            desde = LocalDate.parse(desdeStr);
        }
        LocalDate hasta = null;
        if (hastaStr != null && !hastaStr.isEmpty()) {
            hasta = LocalDate.parse(hastaStr);
        }

        // Tratar cadenas vacías como null
        String processedEstado = (estado != null && estado.isEmpty()) ? null : estado;
        String processedEmail = (usuarioEmail != null && usuarioEmail.isEmpty()) ? null : usuarioEmail;

        // Obtener pedidos filtrados
        List<Pedido> pedidos = pedidoService.filtrarPedidos(desde, hasta, processedEstado, processedEmail);

        // Convertir a Map con fechas formateadas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Map<String, Object>> pedidosFormateados = pedidos.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idPedido", p.getIdPedido());
            map.put("fechaPedido", p.getFechaPedido() != null ? p.getFechaPedido().format(formatter) : "");
            map.put("usuarioEmail", p.getUsuario() != null ? p.getUsuario().getEmail() : "N/A");
            map.put("direccionOrigen", (p.getDireccionOrigen() != null ? p.getDireccionOrigen().getCalle() : "") + " " +
                    (p.getDireccionOrigen() != null ? p.getDireccionOrigen().getNumero() : ""));
            map.put("direccionDestino",
                    (p.getDireccionDestino() != null ? p.getDireccionDestino().getCalle() : "") + " " +
                            (p.getDireccionDestino() != null ? p.getDireccionDestino().getNumero() : ""));
            map.put("estado", p.getEstado() != null ? p.getEstado().getNombre() : "N/A");
            map.put("totalFinal", p.getTotalFinal() != null ? p.getTotalFinal() : 0);
            return map;
        }).collect(java.util.stream.Collectors.toList());

        // Generar PDF
        pdfGenerator.generarPdf("reporte-pedidos", "pedidos", pedidosFormateados, desde, hasta, response);
    }

    /**
     * Genera y descarga el reporte PDF de vehículos
     * GET /reporte-vehiculos
     */
    @GetMapping("/reporte-vehiculos")
    public void reporteVehiculos(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String modelo,
            HttpServletResponse response) throws Exception {

        // Tratar cadenas vacías como null
        String processedPlaca = (placa != null && placa.isEmpty()) ? null : placa;
        String processedModelo = (modelo != null && modelo.isEmpty()) ? null : modelo;

        // Obtener vehículos filtrados
        List<Vehiculo> vehiculos = vehiculoService.filtrarVehiculos(processedPlaca, processedModelo);

        // Generar PDF
        pdfGenerator.generarPdf("reporte-vehiculos", "vehiculos", vehiculos, null, null, response);
    }

    /**
     * Genera y descarga el reporte PDF de rutas
     * GET /reporte-rutas
     */
    @GetMapping("/reporte-rutas")
    public void reporteRutas(
            @RequestParam(required = false) String fechaStr,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String conductorEmail,
            HttpServletResponse response) throws Exception {

        // Parsear fecha
        LocalDate fecha = null;
        if (fechaStr != null && !fechaStr.isEmpty()) {
            fecha = LocalDate.parse(fechaStr);
        }

        // Tratar cadenas vacías como null
        String processedEstado = (estado != null && estado.isEmpty()) ? null : estado;
        String processedEmail = (conductorEmail != null && conductorEmail.isEmpty()) ? null : conductorEmail;

        // Obtener rutas filtradas
        List<Ruta> rutas = rutaService.filtrarRutas(fecha, processedEstado, processedEmail);

        // Convertir a Map con fechas formateadas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Map<String, Object>> rutasFormateadas = rutas.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idRuta", r.getIdRuta() != null ? r.getIdRuta() : 0);
            map.put("nombreRuta", r.getNombreRuta() != null ? r.getNombreRuta() : "N/A");
            map.put("vehiculoPlaca",
                    (r.getVehiculo() != null && r.getVehiculo().getPlaca() != null) ? r.getVehiculo().getPlaca()
                            : "N/A");
            String conductorNombre = "";
            if (r.getConductor() != null) {
                conductorNombre = (r.getConductor().getPrimerNombre() != null ? r.getConductor().getPrimerNombre() : "")
                        + " " +
                        (r.getConductor().getPrimerApellido() != null ? r.getConductor().getPrimerApellido() : "");
            }
            map.put("conductorNombre", conductorNombre.trim().isEmpty() ? "N/A" : conductorNombre.trim());
            map.put("statusRuta", r.getStatusRuta() != null ? r.getStatusRuta() : "N/A");
            map.put("fechaInicio", r.getFechaInicio() != null ? r.getFechaInicio().format(formatter) : "");
            return map;
        }).collect(java.util.stream.Collectors.toList());

        // Generar PDF
        pdfGenerator.generarPdf("reporte-rutas", "rutas", rutasFormateadas, fecha, null, response);
    }
}
