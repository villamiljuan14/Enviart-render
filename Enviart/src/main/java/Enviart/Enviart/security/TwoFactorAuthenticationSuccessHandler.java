package Enviart.Enviart.security;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.repository.UsuarioRepository;
import Enviart.Enviart.service.TwoFactorService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TwoFactorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TwoFactorService twoFactorService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && Boolean.TRUE.equals(usuario.getTwoFactorEnabled())) {
            // Generate and send code
            twoFactorService.generateAndSendCode(usuario);

            // Mark session as needing 2FA
            request.getSession().setAttribute("is_2fa_verified", false);
            request.getSession().setAttribute("needs_2fa", true);

            response.sendRedirect("/verify-2fa");
        } else {
            // No 2FA needed
            request.getSession().setAttribute("is_2fa_verified", true);
            request.getSession().setAttribute("needs_2fa", false);
            response.sendRedirect("/home");
        }
    }
}
