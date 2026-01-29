
package Enviart.Enviart.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // Enviar email simple (texto plano)
    public void enviarEmail(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mensaje.setFrom("villamiljuan14@gmail.com");
        
        javaMailSender.send(mensaje);
    }

    // Enviar email con HTML
    public void enviarEmailHTML(String destinatario, String asunto, String cuerpo) throws Exception {
        MimeMessage mensaje = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(cuerpo, true); // true = es HTML
        helper.setFrom("villamiljuan14@gmail.com");
        
        javaMailSender.send(mensaje);
    }
}