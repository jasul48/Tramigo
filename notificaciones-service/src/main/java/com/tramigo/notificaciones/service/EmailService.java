package com.tramigo.notificaciones.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envío de correos (migrado de EmailService del monolito).
 * Si no hay cuenta SMTP configurada, el envío se omite sin romper el flujo.
 */
@Service
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String senderEmail;
    private final String soporteEmail;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${spring.mail.username:}") String senderEmail,
                        @Value("${tramigo.notificaciones.soporte-email:}") String soporteEmail) {
        this.mailSenderProvider = mailSenderProvider;
        this.senderEmail = senderEmail;
        // Si no se define un correo de soporte, se envía a la propia cuenta remitente
        this.soporteEmail = (soporteEmail == null || soporteEmail.isBlank()) ? senderEmail : soporteEmail;
    }

    public boolean estaConfigurado() {
        return mailSenderProvider.getIfAvailable() != null
                && senderEmail != null && !senderEmail.isBlank()
                && soporteEmail != null && !soporteEmail.isBlank();
    }

    public String getSoporteEmail() {
        return soporteEmail;
    }

    /** Envía el mensaje de contacto al correo de soporte. Lanza excepción si el SMTP falla. */
    public void enviarContactoASoporte(String remitenteNombre, String remitenteCorreo, String asunto, String mensaje) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("JavaMailSender no está disponible");
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(soporteEmail);
        mailMessage.setReplyTo(remitenteCorreo);
        mailMessage.setSubject("Nuevo mensaje de Tramigo: " + asunto);
        mailMessage.setText("De: " + remitenteNombre + " <" + remitenteCorreo + ">\n\n" + mensaje);
        mailSender.send(mailMessage);
    }
}
