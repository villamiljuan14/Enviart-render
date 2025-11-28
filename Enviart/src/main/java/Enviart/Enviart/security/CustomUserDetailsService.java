package Enviart.Enviart.security;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscar el usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("FALLO DE LOGIN: Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        // 2. Comprobar que el usuario esté activo
        if (!usuario.getEstadoUsuario()) {
            logger.warn("FALLO DE LOGIN: Usuario con email {} inactivo.", email);
            throw new UsernameNotFoundException("El usuario con email: " + email + " está inactivo.");
        }

        // 3. Obtener el rol y construir la autoridad
        // Spring Security requiere que el rol empiece con "ROLE_"
        String rolName = "ROLE_" + usuario.getRol().getTipoRol().name();

        logger.info("LOGIN EXITOSO: Usuario {} con rol {}", email, rolName);

        // 4. Crear y retornar el objeto UserDetails
        return new User(
                usuario.getEmail(),
                usuario.getContrasenaUsuario(),
                Collections.singletonList(new SimpleGrantedAuthority(rolName)));
    }
}
