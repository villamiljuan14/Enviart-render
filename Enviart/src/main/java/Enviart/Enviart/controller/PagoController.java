package Enviart.Enviart.controller;

import Enviart.Enviart.model.optimizada.Pago;
import Enviart.Enviart.model.optimizada.Pedido;
import Enviart.Enviart.model.optimizada.TipoPago;
import Enviart.Enviart.service.PagoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public String listarPagos(Model model) {
        List<Pago> pagos = pagoService.listarPagos();
        model.addAttribute("pagos", pagos);
        return "pagos";
    }

    @GetMapping("/pedido/{idPedido}")
    public String listarPagosPorPedido(@PathVariable Integer idPedido, Model model) {
        List<Pago> pagos = pagoService.listarPagosPorPedido(idPedido);
        model.addAttribute("pagos", pagos);
        model.addAttribute("idPedido", idPedido);
        return "pagos";
    }

    @GetMapping("/nuevo")
    public String nuevoPago(Model model) {
        model.addAttribute("pago", new Pago());
        model.addAttribute("accion", "/pagos/guardar");

        List<Pedido> pedidos = pagoService.listarPedidos();
        List<TipoPago> tiposPago = pagoService.listarTiposPago();

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("tiposPago", tiposPago);

        return "pago-form";
    }

    @GetMapping("/editar/{id}")
    public String editarPago(@PathVariable Integer id, Model model) {
        return pagoService.buscarPorId(id)
                .map(pago -> {
                    model.addAttribute("pago", pago);
                    model.addAttribute("accion", "/pagos/guardar");

                    List<Pedido> pedidos = pagoService.listarPedidos();
                    List<TipoPago> tiposPago = pagoService.listarTiposPago();

                    model.addAttribute("pedidos", pedidos);
                    model.addAttribute("tiposPago", tiposPago);

                    return "pago-form";
                })
                .orElse("redirect:/pagos");
    }

    @PostMapping("/guardar")
    public String guardarPago(
            @ModelAttribute("pago") Pago pago,
            @RequestParam(value = "pedido.idPedido", required = false) Integer pedidoId,
            @RequestParam(value = "tipoPago.idTipoPago", required = false) Integer tipoPagoId,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            if (pedidoId != null) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(pedidoId);
                pago.setPedido(pedido);
            }

            if (tipoPagoId != null) {
                TipoPago tipoPago = new TipoPago();
                tipoPago.setIdTipoPago(tipoPagoId);
                pago.setTipoPago(tipoPago);
            }

            pagoService.guardarPago(pago);
            redirectAttributes.addFlashAttribute("mensaje", "Pago guardado exitosamente");
            return "redirect:/pagos";
        } catch (Exception e) {
            model.addAttribute("pago", pago);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accion", "/pagos/guardar");

            List<Pedido> pedidos = pagoService.listarPedidos();
            List<TipoPago> tiposPago = pagoService.listarTiposPago();

            model.addAttribute("pedidos", pedidos);
            model.addAttribute("tiposPago", tiposPago);

            return "pago-form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPago(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            pagoService.eliminarPago(id);
            redirectAttributes.addFlashAttribute("mensaje", "Pago eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar pago: " + e.getMessage());
        }
        return "redirect:/pagos";
    }
}
