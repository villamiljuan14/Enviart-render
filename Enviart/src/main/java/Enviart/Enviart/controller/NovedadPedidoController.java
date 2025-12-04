package Enviart.Enviart.controller;

import Enviart.Enviart.model.optimizada.NovedadPedido;
import Enviart.Enviart.model.optimizada.Pedido;
import Enviart.Enviart.service.NovedadPedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/novedades")
public class NovedadPedidoController {

    private final NovedadPedidoService novedadService;

    public NovedadPedidoController(NovedadPedidoService novedadService) {
        this.novedadService = novedadService;
    }

    @GetMapping
    public String listarNovedades(Model model) {
        List<NovedadPedido> novedades = novedadService.listarNovedades();
        model.addAttribute("novedades", novedades);
        return "novedades";
    }

    @GetMapping("/pedido/{idPedido}")
    public String listarNovedadesPorPedido(@PathVariable Integer idPedido, Model model) {
        List<NovedadPedido> novedades = novedadService.listarNovedadesPorPedido(idPedido);
        model.addAttribute("novedades", novedades);
        model.addAttribute("idPedido", idPedido);
        return "novedades";
    }

    @GetMapping("/nuevo")
    public String nuevaNovedad(Model model) {
        model.addAttribute("novedad", new NovedadPedido());
        model.addAttribute("accion", "/novedades/guardar");

        List<Pedido> pedidos = novedadService.listarPedidos();
        model.addAttribute("pedidos", pedidos);

        return "novedad-form";
    }

    @GetMapping("/editar/{id}")
    public String editarNovedad(@PathVariable Integer id, Model model) {
        return novedadService.buscarPorId(id)
                .map(novedad -> {
                    model.addAttribute("novedad", novedad);
                    model.addAttribute("accion", "/novedades/guardar");

                    List<Pedido> pedidos = novedadService.listarPedidos();
                    model.addAttribute("pedidos", pedidos);

                    return "novedad-form";
                })
                .orElse("redirect:/novedades");
    }

    @PostMapping("/guardar")
    public String guardarNovedad(
            @ModelAttribute("novedad") NovedadPedido novedad,
            @RequestParam(value = "pedido.idPedido", required = false) Integer pedidoId,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            if (pedidoId != null) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(pedidoId);
                novedad.setPedido(pedido);
            }

            novedadService.guardarNovedad(novedad);
            redirectAttributes.addFlashAttribute("mensaje", "Novedad guardada exitosamente");
            return "redirect:/novedades";
        } catch (Exception e) {
            model.addAttribute("novedad", novedad);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accion", "/novedades/guardar");

            List<Pedido> pedidos = novedadService.listarPedidos();
            model.addAttribute("pedidos", pedidos);

            return "novedad-form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarNovedad(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            novedadService.eliminarNovedad(id);
            redirectAttributes.addFlashAttribute("mensaje", "Novedad eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar novedad: " + e.getMessage());
        }
        return "redirect:/novedades";
    }
}
