package Enviart.Enviart.controller;

import Enviart.Enviart.dto.RegistroDTO;
import Enviart.Enviart.service.UsuarioService;
import Enviart.Enviart.repository.RolRepository;
import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.Rol;
import Enviart.Enviart.util.validation.PasswordValidator;
import Enviart.Enviart.util.enums.TipoDocumento;
import Enviart.Enviart.util.enums.TipoRol;
import Enviart.Enviart.util.enums.TipoDocumento;
import Enviart.Enviart.util.enums.TipoRol;
import Enviart.Enviart.util.enums.EstadoEnvio;
import Enviart.Enviart.repository.EnvioRepository;
import Enviart.Enviart.repository.VehiculoRepository;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

@Controller
public class HomeController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final EnvioRepository envioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final Enviart.Enviart.repository.RutaRepository rutaRepository;
    private final Enviart.Enviart.service.PedidoService pedidoService;

    public HomeController(UsuarioService usuarioService, RolRepository rolRepository,
            EnvioRepository envioRepository, VehiculoRepository vehiculoRepository,
            Enviart.Enviart.repository.RutaRepository rutaRepository,
            Enviart.Enviart.service.PedidoService pedidoService) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.envioRepository = envioRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.rutaRepository = rutaRepository;
        this.pedidoService = pedidoService;
    }

    // --- Rutas Públicas ---

    @GetMapping({ "/", "/index" })
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new RegistroDTO());
        return "register";
    }

    @GetMapping("/tracking-demo")
    public String trackingDemo() {
        return "tracking-demo";
    }

    @PostMapping("/register")
    public String registerPost(
            @Valid @ModelAttribute("user") RegistroDTO registroDTO,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validar que las contraseñas coincidan
        if (!registroDTO.getPassword().equals(registroDTO.getPasswordConfirm())) {
            model.addAttribute("errorMessage", "Las contraseñas no coinciden");
            model.addAttribute("user", registroDTO);
            return "register";
        }

        // Validar política de contraseña
        String passwordError = PasswordValidator.getErrorMessage(registroDTO.getPassword());
        if (passwordError != null) {
            model.addAttribute("errorMessage", passwordError);
            model.addAttribute("user", registroDTO);
            return "register";
        }

        // Verificar si el email ya existe
        if (usuarioService.buscarPorEmail(registroDTO.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "El email ya está registrado");
            model.addAttribute("user", registroDTO);
            return "register";
        }

        // Verificar si el documento ya existe
        if (usuarioService.buscarPorDocumento(registroDTO.getDocUsuario()).isPresent()) {
            model.addAttribute("errorMessage", "El documento ya está registrado");
            model.addAttribute("user", registroDTO);
            return "register";
        }

        try {
            // Crear nuevo usuario con todos los campos del formulario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setTipoDocumento(registroDTO.getTipoDocumento());
            nuevoUsuario.setDocUsuario(registroDTO.getDocUsuario());
            nuevoUsuario.setPrimerNombre(registroDTO.getPrimerNombre());
            nuevoUsuario.setSegundoNombre(registroDTO.getSegundoNombre());
            nuevoUsuario.setPrimerApellido(registroDTO.getPrimerApellido());
            nuevoUsuario.setSegundoApellido(registroDTO.getSegundoApellido());
            nuevoUsuario.setTelefono(registroDTO.getTelefono());
            nuevoUsuario.setEmail(registroDTO.getEmail());
            nuevoUsuario.setContrasenaUsuario(registroDTO.getPassword()); // Se encriptará en el servicio
            nuevoUsuario.setEstadoUsuario(true);

            // Asignar rol CLIENTE por defecto
            Rol rolCliente = rolRepository.findByTipoRol(TipoRol.CLIENTE)
                    .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
            nuevoUsuario.setRol(rolCliente);

            usuarioService.registrarUsuario(nuevoUsuario);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Cuenta creada exitosamente. Por favor inicia sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al crear la cuenta: " + e.getMessage());
            model.addAttribute("user", registroDTO);
            return "register";
        }
    }

    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            model.addAttribute("email", email);

            // Obtener el rol del usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse("USER");

            // Limpiar el rol (quitar ROLE_)
            String cleanRole = role.replace("ROLE_", "");
            System.out.println("DEBUG: User " + email + " has role: " + role + " (Clean: " + cleanRole + ")");
            model.addAttribute("role", cleanRole);

            // Redirección basada en Rol
            if ("CLIENTE".equals(cleanRole)) {
                System.out.println("DEBUG: Redirecting to home-cliente");
                model.addAttribute("nombre", "Usuario Cliente");

                // Fetch real data for Client
                List<Enviart.Enviart.model.optimizada.Pedido> pedidosCliente = pedidoService
                        .obtenerPedidosPorUsuario(email);

                long totalEnvios = pedidosCliente.size();
                long enTransitoCliente = pedidosCliente.stream()
                        .filter(p -> "EN_TRANSITO".equals(p.getEstado().getNombre())
                                || "RECEPCIONADO".equals(p.getEstado().getNombre()))
                        .count();
                long conNovedad = 0L; // TODO: Implement novedad count
                long creadosMes = pedidosCliente.stream()
                        .filter(p -> p.getFechaPedido().getMonth() == java.time.LocalDateTime.now().getMonth())
                        .count();

                BigDecimal gastoTotalMes = pedidosCliente.stream()
                        .filter(p -> p.getFechaPedido().getMonth() == java.time.LocalDateTime.now().getMonth())
                        .map(Enviart.Enviart.model.optimizada.Pedido::getTotalFinal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                model.addAttribute("totalEnvios", totalEnvios);
                model.addAttribute("enTransito", enTransitoCliente);
                model.addAttribute("conNovedad", conNovedad);
                model.addAttribute("creadosMes", creadosMes > 0 ? (creadosMes * 100 / Math.max(totalEnvios, 1)) : 0);
                model.addAttribute("gastoTotalMes", gastoTotalMes);
                model.addAttribute("facturasPendientes", enTransitoCliente);
                model.addAttribute("pendientesCalificar", 0L);

                // Chart data (orders by status) - format expected by dashboard-cliente.js
                java.util.Map<String, Object> chartData = new java.util.LinkedHashMap<>();
                long entregados = pedidosCliente.stream().filter(p -> "ENTREGADO".equals(p.getEstado().getNombre()))
                        .count();
                long pendientes = pedidosCliente.stream().filter(p -> "RECEPCIONADO".equals(p.getEstado().getNombre()))
                        .count();

                chartData.put("series", new Long[] { entregados, enTransitoCliente, 0L, pendientes });
                chartData.put("categories", new String[] { "Entregado", "En Tránsito", "Con Novedad", "Pendiente" });
                chartData.put("colors", new String[] { "#10B981", "#3B82F6", "#EF4444", "#FCD34D" });

                try {
                    model.addAttribute("chartDataJson",
                            new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(chartData));
                } catch (Exception e) {
                    model.addAttribute("chartDataJson", "{}");
                }

                // Last order (ultimoPedido) - create a simple DTO-like map
                java.util.Map<String, Object> ultimoPedido = new java.util.HashMap<>();
                if (!pedidosCliente.isEmpty()) {
                    Enviart.Enviart.model.optimizada.Pedido ultimo = pedidosCliente.get(0);
                    ultimoPedido.put("id", ultimo.getIdPedido());
                    ultimoPedido.put("codigo", "#ENV-" + ultimo.getIdPedido());
                    ultimoPedido.put("fechaEstimada", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .format(ultimo.getFechaPedido().plusDays(3)));
                    // Timeline mock
                    java.util.List<java.util.Map<String, String>> timeline = new java.util.ArrayList<>();
                    java.util.Map<String, String> evento1 = new java.util.HashMap<>();
                    evento1.put("fecha", java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                            .format(ultimo.getFechaPedido()));
                    evento1.put("descripcion", "Pedido Recepcionado");
                    timeline.add(evento1);
                    ultimoPedido.put("timeline", timeline);
                } else {
                    ultimoPedido.put("id", 0);
                    ultimoPedido.put("codigo", "#ENV-0000");
                    ultimoPedido.put("fechaEstimada", "N/A");
                    ultimoPedido.put("timeline", java.util.Collections.emptyList());
                }
                model.addAttribute("ultimoPedido", ultimoPedido);

                // Recent orders
                java.util.List<java.util.Map<String, Object>> pedidosRecientes = new java.util.ArrayList<>();
                for (int i = 0; i < Math.min(5, pedidosCliente.size()); i++) {
                    Enviart.Enviart.model.optimizada.Pedido p = pedidosCliente.get(i);
                    java.util.Map<String, Object> reciente = new java.util.HashMap<>();
                    reciente.put("codigo", "#ENV-" + p.getIdPedido());
                    reciente.put("estado", p.getEstado() != null ? p.getEstado().getNombre() : "Pendiente");
                    pedidosRecientes.add(reciente);
                }
                model.addAttribute("pedidosRecientes", pedidosRecientes);

                return "home-cliente";
            } else if ("PROVEEDOR".equals(cleanRole)) {
                System.out.println("DEBUG: Redirecting to home-proveedor");
                model.addAttribute("nombre", "Usuario Proveedor");

                // Fetch orders created by this provider
                List<Enviart.Enviart.model.optimizada.Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(email);

                long totalPedidosProv = pedidos.size();
                long pedidosPendientesProv = pedidos.stream()
                        .filter(p -> !"ENTREGADO".equals(p.getEstado().getNombre())).count();
                // Calculate total revenue (sum of totalFinal)
                BigDecimal totalFacturacionProv = pedidos.stream()
                        .map(Enviart.Enviart.model.optimizada.Pedido::getTotalFinal)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                model.addAttribute("totalPedidos", totalPedidosProv);
                model.addAttribute("pedidosPendientes", pedidosPendientesProv);
                model.addAttribute("totalFacturacion", totalFacturacionProv);

                // Pass recent orders (pending ones first)
                List<Enviart.Enviart.model.optimizada.Pedido> pendientes = pedidos.stream()
                        .filter(p -> !"ENTREGADO".equals(p.getEstado().getNombre()))
                        .limit(5)
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("pedidosPendientesList", pendientes);

                return "home-proveedor";
            } else if ("MENSAJERO".equals(cleanRole)) {
                System.out.println("DEBUG: Redirecting to home-mensajero");

                // Obtener usuario mensajero
                Usuario mensajero = usuarioService.buscarPorEmail(email)
                        .orElse(null);

                if (mensajero != null) {
                    model.addAttribute("nombre", mensajero.getPrimerNombre() + " " + mensajero.getPrimerApellido());
                } else {
                    model.addAttribute("nombre", "Mensajero");
                }

                // Obtener ruta asignada al mensajero - usando búsqueda por ID
                java.util.List<Enviart.Enviart.model.optimizada.Ruta> rutasMensajero = mensajero != null
                        ? rutaRepository.findByConductor_IdUsuario(mensajero.getIdUsuario())
                        : java.util.Collections.emptyList();

                // Datos de rutas y vehículo
                if (!rutasMensajero.isEmpty()) {
                    // Usamos el vehículo de la primera ruta (asumiendo que usa el mismo para todas)
                    Enviart.Enviart.model.optimizada.Ruta rutaPrincipal = rutasMensajero.get(0);
                    Enviart.Enviart.model.optimizada.Vehiculo vehiculo = rutaPrincipal.getVehiculo();

                    // Concatenar nombres de todas las rutas
                    String nombresRutas = rutasMensajero.stream()
                            .map(Enviart.Enviart.model.optimizada.Ruta::getNombreRuta)
                            .collect(java.util.stream.Collectors.joining(", "));

                    model.addAttribute("vehiculoPlaca", vehiculo != null ? vehiculo.getPlaca() : "Sin asignar");
                    model.addAttribute("vehiculoModelo", vehiculo != null ? vehiculo.getModelo() : "N/A");
                    model.addAttribute("rutaNombre", nombresRutas);
                    model.addAttribute("rutaEstado", rutaPrincipal.getStatusRuta()); // Estado de la principal
                    model.addAttribute("tieneRuta", true);
                    model.addAttribute("cantidadRutas", rutasMensajero.size());
                } else {
                    model.addAttribute("vehiculoPlaca", "Sin asignar");
                    model.addAttribute("vehiculoModelo", "N/A");
                    model.addAttribute("rutaNombre", "Sin ruta asignada");
                    model.addAttribute("rutaEstado", "N/A");
                    model.addAttribute("tieneRuta", false);
                    model.addAttribute("cantidadRutas", 0);
                }

                // Obtener pedidos asignados al mensajero (a través de sus rutas)
                java.util.List<Enviart.Enviart.model.optimizada.Pedido> pedidosMensajero = mensajero != null
                        ? pedidoService.obtenerPedidosPorConductor(mensajero.getIdUsuario())
                        : java.util.Collections.emptyList();

                System.out.println(
                        "DEBUG [Mensajero Dashboard]: Pedidos asignados encontrados=" + pedidosMensajero.size());

                long pedidosAsignadosHoy = pedidosMensajero.size();
                long entregasPendientes = pedidosMensajero.stream()
                        .filter(p -> !"ENTREGADO".equals(p.getEstado().getNombre())
                                && !"CANCELADO".equals(p.getEstado().getNombre()))
                        .count();
                long entregadosHoy = pedidosMensajero.stream()
                        .filter(p -> "ENTREGADO".equals(p.getEstado().getNombre()))
                        .count();

                // Calcular tasa de entrega exitosa basada en sus propios pedidos
                long totalHistorico = pedidosMensajero.size();
                long totalEntregados = entregadosHoy;
                int tasaExito = totalHistorico > 0 ? (int) ((totalEntregados * 100) / totalHistorico) : 0;

                model.addAttribute("pedidosAsignadosHoy", pedidosAsignadosHoy);
                model.addAttribute("entregasPendientes", entregasPendientes);
                model.addAttribute("entregadosHoy", entregadosHoy);
                model.addAttribute("tasaExito", tasaExito);
                model.addAttribute("entregasMes", totalEntregados);

                // Pedidos recientes para la tabla (los que no están entregados)
                java.util.List<Enviart.Enviart.model.optimizada.Pedido> pedidosPendientes = pedidosMensajero.stream()
                        .filter(p -> !"ENTREGADO".equals(p.getEstado().getNombre()))
                        .limit(5)
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("pedidosPendientesMensajero", pedidosPendientes);

                return "home-mensajero";
            }

            // Lógica para ADMINISTRADOR (y otros roles por defecto)
            // Agregar estadísticas reales
            model.addAttribute("totalUsuarios", usuarioService.listarUsuarios().size());

            // Estadísticas Dashboard - Usando modelo de Pedidos
            java.util.List<Enviart.Enviart.model.optimizada.Pedido> todosPedidos = pedidoService.obtenerTodosPedidos();
            long totalPedidos = todosPedidos.size();
            long enTransito = todosPedidos.stream()
                    .filter(p -> "EN_TRANSITO".equals(p.getEstado().getNombre()))
                    .count();
            long entregados = todosPedidos.stream()
                    .filter(p -> "ENTREGADO".equals(p.getEstado().getNombre()))
                    .count();
            BigDecimal ingresos = todosPedidos.stream()
                    .map(Enviart.Enviart.model.optimizada.Pedido::getTotalFinal)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long totalVehiculos = vehiculoRepository.count();

            // Estadísticas de Rutas
            long totalRutas = rutaRepository.count();
            long rutasActivas = rutaRepository.findAll().stream()
                    .filter(r -> "EN_CURSO".equals(r.getStatusRuta()))
                    .count();

            model.addAttribute("totalPedidos", totalPedidos);
            model.addAttribute("enTransito", enTransito);
            model.addAttribute("entregados", entregados);
            model.addAttribute("ingresosTotales", ingresos);
            model.addAttribute("totalVehiculos", totalVehiculos);
            model.addAttribute("totalRutas", totalRutas);
            model.addAttribute("rutasActivas", rutasActivas);

            // Datos para la Gráfica (Pedidos por mes)
            Long[] dataEnvios = new Long[12];
            Long[] dataEntregas = new Long[12];
            java.util.Arrays.fill(dataEnvios, 0L);
            java.util.Arrays.fill(dataEntregas, 0L);

            // Agrupar pedidos por mes
            for (Enviart.Enviart.model.optimizada.Pedido p : todosPedidos) {
                if (p.getFechaPedido() != null) {
                    int mes = p.getFechaPedido().getMonthValue();
                    if (mes >= 1 && mes <= 12) {
                        dataEnvios[mes - 1]++;
                        if ("ENTREGADO".equals(p.getEstado().getNombre())) {
                            dataEntregas[mes - 1]++;
                        }
                    }
                }
            }

            model.addAttribute("chartDataEnvios", dataEnvios);
            model.addAttribute("chartDataEntregas", dataEntregas);

            return "home";
        }
        return "redirect:/login";
    }

    @GetMapping("/home-legacy")
    public String homeLegacy(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            model.addAttribute("email", email);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse("USER");

            String cleanRole = role.replace("ROLE_", "");
            model.addAttribute("role", cleanRole);
            model.addAttribute("totalUsuarios", usuarioService.listarUsuarios().size());

            return "home-legacy";
        }
        return "redirect:/login";
    }

    // --- Rutas de Gestión de Usuarios ---

    @GetMapping("/usuarios")
    public String listarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String desdeStr,
            @RequestParam(required = false) String hastaStr,
            @RequestParam(required = false) String rol,
            Model model) {

        // Tratar cadenas vacías como null para los filtros
        String processedNombre = (nombre != null && nombre.isEmpty()) ? null : nombre;
        String processedRol = (rol != null && rol.isEmpty()) ? null : rol;

        // Parsear fechas de String a LocalDate de forma segura
        java.time.LocalDate desde = null;
        if (desdeStr != null && !desdeStr.isEmpty()) {
            desde = java.time.LocalDate.parse(desdeStr);
        }
        java.time.LocalDate hasta = null;
        if (hastaStr != null && !hastaStr.isEmpty()) {
            hasta = java.time.LocalDate.parse(hastaStr);
        }

        // Obtener usuarios filtrados
        java.util.List<Usuario> usuarios = usuarioService.filtrarUsuarios(processedNombre, desde, hasta, processedRol);

        model.addAttribute("usuarios", usuarios);

        // Agregar datos al modelo para mantener el estado de los filtros en la vista
        model.addAttribute("nombre", processedNombre);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("rol", processedRol);

        return "usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("accion", "/usuarios/guardar");
        model.addAttribute("listaRoles", rolRepository.findAll());
        return "form";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("accion", "/usuarios/guardar");
                    model.addAttribute("listaRoles", rolRepository.findAll());
                    return "form";
                })
                .orElse("redirect:/usuarios");
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(
            @ModelAttribute("usuario") Usuario usuario,
            @RequestParam(value = "rol.idRol", required = false) Integer rolId,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // Cargar el rol completo desde el ID
            if (rolId != null) {
                Rol rol = rolRepository.findById(rolId)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                usuario.setRol(rol);
            }

            usuarioService.registrarUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario guardado exitosamente");
            return "redirect:/usuarios";
        } catch (Exception e) {
            // Mantener los datos del formulario y mostrar el error
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accion", "/usuarios/guardar");
            model.addAttribute("listaRoles", rolRepository.findAll());
            return "form";
        }
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            // DEBES ASEGURARTE DE QUE ESTE MÉTODO EXISTA EN TU UsuarioService
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
