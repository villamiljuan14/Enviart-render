package Enviart.Enviart.controller;

import Enviart.Enviart.dto.RegistroDTO;
import Enviart.Enviart.service.UsuarioService;
import Enviart.Enviart.repository.RolRepository;
import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.Rol;
import Enviart.Enviart.util.validation.PasswordValidator;
import Enviart.Enviart.util.enums.TipoDocumento;
import Enviart.Enviart.util.enums.TipoRol;
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

    @Autowired
    public HomeController(UsuarioService usuarioService, RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
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

        try {
            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(registroDTO.getEmail());
            nuevoUsuario.setContrasenaUsuario(registroDTO.getPassword()); // Se encriptará en el servicio

            // Asignar rol CLIENTE por defecto
            Rol rolCliente = rolRepository.findByTipoRol(TipoRol.CLIENTE)
                    .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
            nuevoUsuario.setRol(rolCliente);

            // Datos básicos requeridos
            nuevoUsuario.setTipoDocumento(TipoDocumento.CC);
            // Generar documento único temporal basado en timestamp
            nuevoUsuario.setDocUsuario("TEMP" + System.currentTimeMillis());
            nuevoUsuario.setPrimerNombre("Usuario");
            nuevoUsuario.setPrimerApellido("Nuevo");
            nuevoUsuario.setTelefono("0000000000");
            nuevoUsuario.setEstadoUsuario(true);

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
                // Puedes agregar datos específicos para el cliente aquí
                // Por ejemplo: model.addAttribute("misEnvios",
                // envioService.obtenerEnviosPorCliente(email));
                // Por ahora usamos datos mock en el template
                model.addAttribute("nombre", "Usuario Cliente"); // Idealmente obtener del servicio
                return "home-cliente";
            } else if ("PROVEEDOR".equals(cleanRole)) {
                System.out.println("DEBUG: Redirecting to home-proveedor");
                // Agregar datos específicos para el proveedor
                model.addAttribute("nombre", "Usuario Proveedor");
                return "home-proveedor";
            } else if ("MENSAJERO".equals(cleanRole)) {
                System.out.println("DEBUG: Redirecting to home-mensajero");
                // Agregar datos específicos para el mensajero
                model.addAttribute("nombre", "Usuario Mensajero");
                return "home-mensajero";
            }

            // Lógica para ADMINISTRADOR (y otros roles por defecto)
            // Agregar estadísticas reales
            model.addAttribute("totalUsuarios", usuarioService.listarUsuarios().size());
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
