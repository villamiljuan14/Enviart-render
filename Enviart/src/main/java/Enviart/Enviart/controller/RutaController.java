package Enviart.Enviart.controller;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.optimizada.Ruta;
import Enviart.Enviart.model.optimizada.Vehiculo;
import Enviart.Enviart.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/rutas")
public class RutaController {

    private final RutaService rutaService;

    @Autowired
    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping
    public String listarRutas(
            @RequestParam(required = false) String fechaStr,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String conductorEmail,
            Model model) {

        // Parsear fecha
        java.time.LocalDate fecha = null;
        if (fechaStr != null && !fechaStr.isEmpty()) {
            fecha = java.time.LocalDate.parse(fechaStr);
        }

        // Tratar cadenas vacías como null
        String processedEstado = (estado != null && estado.isEmpty()) ? null : estado;
        String processedEmail = (conductorEmail != null && conductorEmail.isEmpty()) ? null : conductorEmail;

        List<Ruta> rutas = rutaService.filtrarRutas(fecha, processedEstado, processedEmail);
        model.addAttribute("rutas", rutas);

        // Mantener filtros en la vista
        model.addAttribute("fecha", fecha);
        model.addAttribute("estado", processedEstado);
        model.addAttribute("conductorEmail", processedEmail);

        return "rutas";
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/nuevo")
    public String nuevaRuta(Model model) {
        model.addAttribute("ruta", new Ruta());
        model.addAttribute("accion", "/rutas/guardar");

        // Cargar listas para los selects
        List<Vehiculo> vehiculos = rutaService.listarVehiculos();
        List<Usuario> conductores = rutaService.listarConductores();

        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("conductores", conductores);

        return "ruta-form";
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/editar/{id}")
    public String editarRuta(@PathVariable Integer id, Model model) {
        return rutaService.buscarPorId(id)
                .map(ruta -> {
                    model.addAttribute("ruta", ruta);
                    model.addAttribute("accion", "/rutas/guardar");

                    // Cargar listas para los selects
                    List<Vehiculo> vehiculos = rutaService.listarVehiculos();
                    List<Usuario> conductores = rutaService.listarConductores();

                    model.addAttribute("vehiculos", vehiculos);
                    model.addAttribute("conductores", conductores);

                    return "ruta-form";
                })
                .orElse("redirect:/rutas");
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/guardar")
    public String guardarRuta(
            @ModelAttribute("ruta") Ruta ruta,
            @RequestParam(value = "vehiculo.idVehiculo", required = false) Integer vehiculoId,
            @RequestParam(value = "conductor.idUsuario", required = false) Integer conductorId,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // Asignar vehículo y conductor desde los IDs
            if (vehiculoId != null) {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setIdVehiculo(vehiculoId);
                ruta.setVehiculo(vehiculo);
            }

            if (conductorId != null) {
                Usuario conductor = new Usuario();
                conductor.setIdUsuario(conductorId);
                ruta.setConductor(conductor);
            }

            rutaService.guardarRuta(ruta);
            redirectAttributes.addFlashAttribute("mensaje", "Ruta guardada exitosamente");
            return "redirect:/rutas";
        } catch (Exception e) {
            model.addAttribute("ruta", ruta);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accion", "/rutas/guardar");

            // Recargar listas para los selects
            List<Vehiculo> vehiculos = rutaService.listarVehiculos();
            List<Usuario> conductores = rutaService.listarConductores();

            model.addAttribute("vehiculos", vehiculos);
            model.addAttribute("conductores", conductores);

            return "ruta-form";
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/eliminar/{id}")
    public String eliminarRuta(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            rutaService.eliminarRuta(id);
            redirectAttributes.addFlashAttribute("mensaje", "Ruta eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar ruta: " + e.getMessage());
        }
        return "redirect:/rutas";
    }
}
