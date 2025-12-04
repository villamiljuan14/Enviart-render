package Enviart.Enviart.controller;

import Enviart.Enviart.model.optimizada.TipoPago;
import Enviart.Enviart.service.TipoPagoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tipos-pago")
public class TipoPagoController {

    private final TipoPagoService tipoPagoService;

    public TipoPagoController(TipoPagoService tipoPagoService) {
        this.tipoPagoService = tipoPagoService;
    }

    @GetMapping
    public String listarTiposPago(Model model) {
        List<TipoPago> tiposPago = tipoPagoService.listarTiposPago();
        model.addAttribute("tiposPago", tiposPago);
        return "tipos-pago";
    }

    @GetMapping("/nuevo")
    public String nuevoTipoPago(Model model) {
        model.addAttribute("tipoPago", new TipoPago());
        model.addAttribute("accion", "/tipos-pago/guardar");
        return "tipo-pago-form";
    }

    @GetMapping("/editar/{id}")
    public String editarTipoPago(@PathVariable Integer id, Model model) {
        return tipoPagoService.buscarPorId(id)
                .map(tipoPago -> {
                    model.addAttribute("tipoPago", tipoPago);
                    model.addAttribute("accion", "/tipos-pago/guardar");
                    return "tipo-pago-form";
                })
                .orElse("redirect:/tipos-pago");
    }

    @PostMapping("/guardar")
    public String guardarTipoPago(@ModelAttribute("tipoPago") TipoPago tipoPago,
            RedirectAttributes redirectAttributes, Model model) {
        try {
            tipoPagoService.guardarTipoPago(tipoPago);
            redirectAttributes.addFlashAttribute("mensaje", "Tipo de pago guardado exitosamente");
            return "redirect:/tipos-pago";
        } catch (Exception e) {
            model.addAttribute("tipoPago", tipoPago);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accion", "/tipos-pago/guardar");
            return "tipo-pago-form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarTipoPago(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            tipoPagoService.eliminarTipoPago(id);
            redirectAttributes.addFlashAttribute("mensaje", "Tipo de pago eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar tipo de pago: " + e.getMessage());
        }
        return "redirect:/tipos-pago";
    }
}
