package Enviart.Enviart.service;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TwoFactorService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    public void generateAndSendCode(Usuario usuario) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000); // 6 digits
        usuario.setVerificationCode(code);
        usuario.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10)); // 10 mins expiry
        usuarioRepository.save(usuario);

        String text = "Su código de verificación es: " + code + "\n\nEste código expira en 10 minutos.";
        emailService.enviarEmail(usuario.getEmail(), "Código de Verificación", text);
    }

    public boolean verifyCode(Usuario usuario, String code) {
        if (usuario.getVerificationCode() == null || usuario.getVerificationCodeExpiresAt() == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(usuario.getVerificationCodeExpiresAt())) {
            return false;
        }

        if (usuario.getVerificationCode().equals(code)) {
            // Clear code after successful verification
            usuario.setVerificationCode(null);
            usuario.setVerificationCodeExpiresAt(null);
            usuarioRepository.save(usuario);
            return true;
        }

        return false;
    }
}
