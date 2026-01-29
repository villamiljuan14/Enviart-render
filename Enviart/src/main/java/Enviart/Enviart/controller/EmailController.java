package Enviart.Enviart.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Enviart.Enviart.service.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    @PostMapping("/enviar")
    public String enviarEmail(@RequestParam String destinatario, 
                              @RequestParam String asunto, 
                              @RequestParam String cuerpo) {
        logger.info("Recibida solicitud para enviar email a: " + destinatario);
        try {
            emailService.enviarEmail(destinatario, asunto, cuerpo);
            return "Email enviado correctamente";
        } catch (Exception e) {
            logger.error("Error en controlador: " + e.getMessage());
            return "Error al enviar email: " + e.getMessage();
        }
    }
}