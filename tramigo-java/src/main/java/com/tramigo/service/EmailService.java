package com.tramigo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:luis.chavarria4142@alumnos.udg.mx}")
    private String senderEmail;

    public void enviarNotificacionContacto(String remitenteNombre, String remitenteCorreo, String asunto, String mensaje) {
        if (mailSender == null) {
            System.out.println("JavaMailSender no está disponible. Omitiendo envío de correo.");
            return;
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(senderEmail); // Se envía al correo de soporte
            mailMessage.setReplyTo(remitenteCorreo);
            mailMessage.setSubject("Nuevo mensaje de Tramigo: " + asunto);
            mailMessage.setText("De: " + remitenteNombre + " <" + remitenteCorreo + ">\n\n" + mensaje);

            mailSender.send(mailMessage);
            System.out.println("Correo de soporte enviado con éxito.");
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de soporte: " + e.getMessage());
        }
    }
}
