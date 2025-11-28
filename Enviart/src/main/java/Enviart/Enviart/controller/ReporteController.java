package Enviart.Enviart.controller;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.service.UsuarioService;
import Enviart.Enviart.util.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para generación de reportes de usuarios
 */
@Controller
public class ReporteController {

    private final UsuarioService usuarioService;
    private final PdfGenerator pdfGenerator;

    public ReporteController(UsuarioService usuarioService, PdfGenerator pdfGenerator) {
        this.usuarioService = usuarioService;
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
        pdfGenerator.generarPdf("reporte-usuarios", usuarios, desde, hasta, response);
    }
}
