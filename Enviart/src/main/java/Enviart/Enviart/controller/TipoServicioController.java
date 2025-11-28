package Enviart.Enviart.controller;

import Enviart.Enviart.model.optimizada.TipoServicio;
import Enviart.Enviart.service.TipoServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tipos-servicio")
public class TipoServicioController {

    private final TipoServicioService tipoServicioService;

    @Autowired
    public TipoServicioController(TipoServicioService tipoServicioService) {
        this.tipoServicioService = tipoServicioService;
    }

    @GetMapping
    public String listarTiposServicio(Model model) {
        List<TipoServicio> tipos = tipoServicioService.listarTiposServicio();
        model.addAttribute("tipos", tipos);
        return "tipos-servicio";
    }

    @GetMapping("/nuevo")
    public String nuevoTipoServicio(Model model) {
        model.addAttribute("tipoServicio", new TipoServicio());
        model.addAttribute("accion", "/tipos-servicio/guardar");
        return "tipo-servicio-form";
    }

    @GetMapping("/editar/{id}")
    public String editarTipoServicio(@PathVariable Integer id, Model model) {
        return tipoServicioService.buscarPorId(id)
                .map(tipo -> {
                    model.addAttribute("tipoServicio", tipo);
                    model.addAttribute("accion", "/tipos-servicio/guardar");
                    return "tipo-servicio-form";
                })
                .orElse("redirect:/tipos-servicio");
    }

    @PostMapping("/guardar")
    public String guardarTipoServicio(@ModelAttribute("tipoServicio") TipoServicio tipoServicio,
            RedirectAttributes redirectAttributes, Model model) {
        try {
            tipoServicioService.guardarTipoServicio(tipoServicio);
            redirectAttributes.addFlashAttribute("mensaje", "Tipo de servicio guardado exitosamente");
            return "redirect:/tipos-servicio";
        } catch (Exception e) {
            model.addAttribute("tipoServicio", tipoServicio);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accion", "/tipos-servicio/guardar");
            return "tipo-servicio-form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarTipoServicio(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            tipoServicioService.eliminarTipoServicio(id);
            redirectAttributes.addFlashAttribute("mensaje", "Tipo de servicio eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar tipo de servicio: " + e.getMessage());
        }
        return "redirect:/tipos-servicio";
    }
}
