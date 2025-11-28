package Enviart.Enviart.controller;

import Enviart.Enviart.model.optimizada.Vehiculo;
import Enviart.Enviart.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public String listarVehiculos(Model model) {
        List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();
        model.addAttribute("vehiculos", vehiculos);
        return "vehiculos";
    }

    @GetMapping("/nuevo")
    public String nuevoVehiculo(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        model.addAttribute("accion", "/vehiculos/guardar");
        return "vehiculo-form";
    }

    @GetMapping("/editar/{id}")
    public String editarVehiculo(@PathVariable Integer id, Model model) {
        return vehiculoService.buscarPorId(id)
                .map(vehiculo -> {
                    model.addAttribute("vehiculo", vehiculo);
                    model.addAttribute("accion", "/vehiculos/guardar");
                    return "vehiculo-form";
                })
                .orElse("redirect:/vehiculos");
    }

    @PostMapping("/guardar")
    public String guardarVehiculo(@ModelAttribute("vehiculo") Vehiculo vehiculo, RedirectAttributes redirectAttributes,
            Model model) {
        try {
            vehiculoService.guardarVehiculo(vehiculo);
            redirectAttributes.addFlashAttribute("mensaje", "Vehículo guardado exitosamente");
            return "redirect:/vehiculos";
        } catch (Exception e) {
            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accion", "/vehiculos/guardar");
            return "vehiculo-form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVehiculo(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            vehiculoService.eliminarVehiculo(id);
            redirectAttributes.addFlashAttribute("mensaje", "Vehículo eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar vehículo: " + e.getMessage());
        }
        return "redirect:/vehiculos";
    }
}
