package com.tramigo.notificaciones.service;

import com.tramigo.notificaciones.model.Notificacion;
import com.tramigo.notificaciones.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final EmailService emailService;

    public NotificacionService(NotificacionRepository notificacionRepository, EmailService emailService) {
        this.notificacionRepository = notificacionRepository;
        this.emailService = emailService;
    }

    /**
     * Registra y envía un mensaje de contacto a soporte.
     * Nunca lanza por fallos de SMTP: deja el resultado (ENVIADA / FALLIDA / OMITIDA) en el historial.
     */
    public Notificacion enviarContacto(String nombre, String correo, String asunto, String mensaje) {
        Notificacion n = new Notificacion();
        n.setTipo(Notificacion.TIPO_CONTACTO);
        n.setDestinatario(emailService.getSoporteEmail());
        n.setRemitenteNombre(recortar(nombre, 100));
        n.setRemitenteCorreo(recortar(correo, 100));
        n.setAsunto(recortar(asunto, 200));
        n.setMensaje(mensaje);

        if (!emailService.estaConfigurado()) {
            n.setEstado(Notificacion.ESTADO_OMITIDA);
            n.setDetalleError("SMTP no configurado (define MAIL_USERNAME y MAIL_PASSWORD).");
            return notificacionRepository.save(n);
        }

        try {
            emailService.enviarContactoASoporte(nombre, correo, asunto, mensaje);
            n.setEstado(Notificacion.ESTADO_ENVIADA);
        } catch (Exception e) {
            n.setEstado(Notificacion.ESTADO_FALLIDA);
            n.setDetalleError(recortar(e.getMessage(), 500));
        }
        return notificacionRepository.save(n);
    }

    public List<Notificacion> listar(String estado) {
        if (estado != null && !estado.isBlank()) {
            return notificacionRepository.findByEstadoOrderByFechaCreacionDesc(estado.trim().toUpperCase());
        }
        return notificacionRepository.findAllByOrderByFechaCreacionDesc();
    }

    public Optional<Notificacion> buscarPorId(Integer id) {
        return notificacionRepository.findById(id);
    }

    private static String recortar(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
