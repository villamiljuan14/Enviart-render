package Enviart.Enviart.service;

import Enviart.Enviart.exception.ReferentialIntegrityException;
import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.repository.PedidoRepository;
import Enviart.Enviart.repository.RutaRepository;
import Enviart.Enviart.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RutaRepository rutaRepository;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            RutaRepository rutaRepository, PedidoRepository pedidoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rutaRepository = rutaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        // Si es un usuario nuevo (sin ID)
        if (usuario.getIdUsuario() == null) {
            if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya está registrado");
            }
            if (usuarioRepository.findByDocUsuario(usuario.getDocUsuario()).isPresent()) {
                throw new RuntimeException("El documento ya está registrado");
            }
            // Encriptar contraseña para nuevo usuario
            usuario.setContrasenaUsuario(passwordEncoder.encode(usuario.getContrasenaUsuario()));
        } else {
            // Es una actualización - verificar que email/documento no estén en uso por OTRO
            // usuario
            usuarioRepository.findByEmail(usuario.getEmail()).ifPresent(existente -> {
                if (!existente.getIdUsuario().equals(usuario.getIdUsuario())) {
                    throw new RuntimeException("El email ya está registrado por otro usuario");
                }
            });
            usuarioRepository.findByDocUsuario(usuario.getDocUsuario()).ifPresent(existente -> {
                if (!existente.getIdUsuario().equals(usuario.getIdUsuario())) {
                    throw new RuntimeException("El documento ya está registrado por otro usuario");
                }
            });

            // Si la contraseña está vacía, mantener la anterior
            if (usuario.getContrasenaUsuario() == null || usuario.getContrasenaUsuario().isEmpty()) {
                Usuario usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                usuario.setContrasenaUsuario(usuarioExistente.getContrasenaUsuario());
            } else {
                // Si hay nueva contraseña, encriptarla
                usuario.setContrasenaUsuario(passwordEncoder.encode(usuario.getContrasenaUsuario()));
            }
        }

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorDocumento(String docUsuario) {
        return usuarioRepository.findByDocUsuario(docUsuario);
    }

    @Transactional
    public void eliminarUsuario(Integer id) {
        // 1. Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Verificar si es conductor en alguna ruta
        long rutasComoConductor = rutaRepository.countByConductor_IdUsuario(id);

        if (rutasComoConductor > 0) {
            throw new ReferentialIntegrityException(
                    String.format("No se puede eliminar el usuario %s %s porque es conductor en %d ruta(s). " +
                            "Por favor, reasigne las rutas antes de eliminar el usuario.",
                            usuario.getPrimerNombre(), usuario.getPrimerApellido(), rutasComoConductor));
        }

        // 3. Verificar si tiene pedidos asociados
        long pedidosAsociados = pedidoRepository.countByUsuario_IdUsuario(id);

        if (pedidosAsociados > 0) {
            throw new ReferentialIntegrityException(
                    String.format("No se puede eliminar el usuario %s %s porque tiene %d pedido(s) asociados. " +
                            "No es posible eliminar usuarios con historial de pedidos.",
                            usuario.getPrimerNombre(), usuario.getPrimerApellido(), pedidosAsociados));
        }

        // 4. Si no tiene dependencias, proceder con la eliminación
        usuarioRepository.deleteById(id);
    }

    /**
     * Filtra usuarios por nombre, rango de fechas y rol
     * 
     * @param nombre Nombre a buscar (coincidencia parcial)
     * @param desde  Fecha desde (opcional)
     * @param hasta  Fecha hasta (opcional)
     * @param rol    Tipo de rol (opcional)
     * @return Lista de usuarios filtrados
     */
    public List<Usuario> filtrarUsuarios(String nombre, java.time.LocalDate desde,
            java.time.LocalDate hasta, String rol) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .filter(u -> {
                    boolean matches = true;

                    // Filtro por nombre (coincidencia parcial case-insensitive)
                    if (nombre != null && !nombre.isEmpty()) {
                        String primerNombre = Optional.ofNullable(u.getPrimerNombre()).orElse("");
                        String segundoNombre = Optional.ofNullable(u.getSegundoNombre()).orElse("");
                        String primerApellido = Optional.ofNullable(u.getPrimerApellido()).orElse("");
                        String segundoApellido = Optional.ofNullable(u.getSegundoApellido()).orElse("");

                        String nombreCompleto = (primerNombre + " " + segundoNombre + " " +
                                primerApellido + " " + segundoApellido).trim().replaceAll("\\s+", " ");

                        if (!nombreCompleto.toLowerCase().contains(nombre.toLowerCase())) {
                            matches = false;
                        }
                    }

                    // Filtro por rango de fechas
                    if (matches && desde != null) {
                        if (u.getCreatedAt() == null || u.getCreatedAt().toLocalDate().isBefore(desde)) {
                            matches = false;
                        }
                    }
                    if (matches && hasta != null) {
                        if (u.getCreatedAt() == null || u.getCreatedAt().toLocalDate().isAfter(hasta)) {
                            matches = false;
                        }
                    }

                    // Filtro por rol
                    if (matches && rol != null && !rol.isEmpty() && !rol.equals("TODOS")) {
                        if (u.getRol() == null || u.getRol().getTipoRol() == null
                                || !u.getRol().getTipoRol().toString().equals(rol)) {
                            matches = false;
                        }
                    }

                    return matches;
                })
                .toList();
    }
}
