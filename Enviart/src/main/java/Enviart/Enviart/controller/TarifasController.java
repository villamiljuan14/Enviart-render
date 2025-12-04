package Enviart.Enviart.controller;

import Enviart.Enviart.model.optimizada.TipoServicio;
import Enviart.Enviart.service.TipoServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controlador para la página de tarifas del proveedor.
 * Muestra las tarifas vigentes del sistema de forma informativa.
 */
@Controller
public class TarifasController {

    private final TipoServicioService tipoServicioService;

    public TarifasController(TipoServicioService tipoServicioService) {
        this.tipoServicioService = tipoServicioService;
    }

    /**
     * Muestra la página de tarifas con todos los tipos de servicio disponibles.
     */
    @GetMapping("/tarifas")
    public String mostrarTarifas(Model model) {
        List<TipoServicio> tiposServicio = tipoServicioService.listarTiposServicio();
        model.addAttribute("tiposServicio", tiposServicio);
        return "tarifas-proveedor";
    }
}
