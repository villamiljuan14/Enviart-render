package Enviart.Enviart.controller;

import Enviart.Enviart.dto.CrearPedidoDTO;
import Enviart.Enviart.model.optimizada.*;
import Enviart.Enviart.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * Listar todos los pedidos del usuario autenticado
     */
    @GetMapping
    public String listarPedidos(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(email);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("email", email);

        return "pedidos-lista";
    }

    /**
     * Mostrar formulario para crear un nuevo pedido
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();

        // Obtener datos necesarios para el formulario
        List<TipoServicio> tiposServicio = pedidoService.obtenerTiposServicio();
        List<Direccion> direcciones = pedidoService.obtenerDireccionesPorUsuario(email);

        // Verificar que existan tipos de servicio
        if (tiposServicio == null || tiposServicio.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No hay tipos de servicio disponibles. Por favor, contacte al administrador para configurar los servicios de envío.");
            return "redirect:/pedidos";
        }

        // Inicializar DTO con estructura vacía para el formulario
        CrearPedidoDTO pedidoDTO = new CrearPedidoDTO();
        pedidoDTO.setPaquetes(new java.util.ArrayList<>());
        pedidoDTO.getPaquetes().add(new Enviart.Enviart.dto.PaqueteDTO());
        pedidoDTO.setDireccionOrigen(new Enviart.Enviart.dto.DireccionDTO());
        pedidoDTO.setDireccionDestino(new Enviart.Enviart.dto.DireccionDTO());

        model.addAttribute("pedidoDTO", pedidoDTO);
        model.addAttribute("tiposServicio", tiposServicio);
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("email", email);

        return "pedido-crear";
    }

    /**
     * Procesar la creación de un nuevo pedido
     */
    @PostMapping("/crear")
    public String crearPedido(
            @Valid @ModelAttribute("pedidoDTO") CrearPedidoDTO pedidoDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Principal principal,
            Model model) {

        System.out.println("Intentando crear pedido...");

        if (principal == null) {
            System.out.println("Usuario no autenticado");
            return "redirect:/login";
        }

        String email = principal.getName();
        System.out.println("Usuario: " + email);

        // Si hay errores de validación, volver al formulario
        if (result.hasErrors()) {
            System.out.println("Errores de validación encontrados: " + result.getErrorCount());
            result.getAllErrors().forEach(error -> {
                System.out.println("Error: " + error.getDefaultMessage());
                System.out.println("Campo: " + error.getObjectName());
            });

            List<TipoServicio> tiposServicio = pedidoService.obtenerTiposServicio();
            List<Direccion> direcciones = pedidoService.obtenerDireccionesPorUsuario(email);

            model.addAttribute("tiposServicio", tiposServicio);
            model.addAttribute("direcciones", direcciones);
            model.addAttribute("email", email);
            model.addAttribute("errorMessage", "Por favor corrija los errores en el formulario.");

            return "pedido-crear";
        }

        try {
            System.out.println("Datos del pedido recibidos: " + pedidoDTO);
            Pedido pedido = pedidoService.crearPedido(pedidoDTO, email);
            System.out.println("Pedido creado con ID: " + pedido.getIdPedido());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Pedido #" + pedido.getIdPedido() + " creado exitosamente. Total: $" + pedido.getTotalFinal());
            return "redirect:/pedidos";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Excepción al crear pedido: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al crear el pedido: " + e.getMessage());
            return "redirect:/pedidos/nuevo";
        }
    }

    /**
     * Ver detalles de un pedido específico
     */
    @GetMapping("/{id}")
    public String verDetallePedido(@PathVariable Integer id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();

        Pedido pedido = pedidoService.obtenerPedidoPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Verificar que el pedido pertenece al usuario autenticado
        if (!pedido.getUsuario().getEmail().equals(email)) {
            return "redirect:/pedidos";
        }

        List<Paquete> paquetes = pedidoService.obtenerPaquetesPorPedido(id);

        model.addAttribute("pedido", pedido);
        model.addAttribute("paquetes", paquetes);
        model.addAttribute("email", email);

        return "pedido-detalle";
    }

    /**
     * Cancelar un pedido
     */
    @PostMapping("/{id}/cancelar")
    public String cancelarPedido(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();

        try {
            Pedido pedido = pedidoService.obtenerPedidoPorId(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            // Verificar que el pedido pertenece al usuario autenticado
            if (!pedido.getUsuario().getEmail().equals(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "No tienes permiso para cancelar este pedido");
                return "redirect:/pedidos";
            }

            pedidoService.cancelarPedido(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido cancelado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cancelar el pedido: " + e.getMessage());
        }

        return "redirect:/pedidos";
    }

    /**
     * Listar todos los pedidos (solo para administradores)
     */
    @GetMapping("/admin/todos")
    public String listarTodosPedidos(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        List<Pedido> pedidos = pedidoService.obtenerTodosPedidos();

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("email", email);

        return "pedidos-admin";
    }

    /**
     * Actualizar estado de un pedido (solo para administradores)
     */
    @PostMapping("/admin/{id}/estado")
    public String actualizarEstado(
            @PathVariable Integer id,
            @RequestParam String nuevoEstado,
            RedirectAttributes redirectAttributes) {

        try {
            pedidoService.actualizarEstadoPedido(id, nuevoEstado);
            redirectAttributes.addFlashAttribute("successMessage", "Estado actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar estado: " + e.getMessage());
        }

        return "redirect:/pedidos/admin/todos";
    }
}
