package Enviart.Enviart.controller;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.repository.UsuarioRepository;
import Enviart.Enviart.service.TwoFactorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TwoFactorController {

    @Autowired
    private TwoFactorService twoFactorService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/verify-2fa")
    public String verify2faPage() {
        return "verify-2fa";
    }

    @PostMapping("/verify-2fa")
    public String verify2fa(@RequestParam("code") String code, Authentication authentication,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && twoFactorService.verifyCode(usuario, code)) {
            // Success
            request.getSession().setAttribute("is_2fa_verified", true);
            return "redirect:/home";
        } else {
            // Failure
            redirectAttributes.addFlashAttribute("error", "Código incorrecto o expirado.");
            return "redirect:/verify-2fa";
        }
    }

    @PostMapping("/2fa/toggle")
    public String toggle2fa(Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null) {
            boolean newState = !Boolean.TRUE.equals(usuario.getTwoFactorEnabled());
            usuario.setTwoFactorEnabled(newState);
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("message",
                    "Autenticación de doble factor " + (newState ? "activada" : "desactivada"));
        }

        return "redirect:/home";
    }

    @GetMapping("/2fa/resend")
    public String resendCode(Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null) {
            twoFactorService.generateAndSendCode(usuario);
            redirectAttributes.addFlashAttribute("message", "Código reenviado exitosamente.");
        }
        return "redirect:/verify-2fa";
    }
}
