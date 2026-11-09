package com.tramigo.soporte.service;

import com.tramigo.soporte.client.NotificacionesClient;
import com.tramigo.soporte.model.Soporte;
import com.tramigo.soporte.repository.SoporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SoporteService {

    public static final List<String> ESTADOS_VALIDOS = List.of(
            Soporte.ESTADO_PENDIENTE, Soporte.ESTADO_EN_PROCESO, Soporte.ESTADO_RESUELTO);

    private final SoporteRepository soporteRepository;
    private final NotificacionesClient notificacionesClient;

    public SoporteService(SoporteRepository soporteRepository, NotificacionesClient notificacionesClient) {
        this.soporteRepository = soporteRepository;
        this.notificacionesClient = notificacionesClient;
    }

    public Soporte crearTicket(String nombre, String correo, String asunto, String mensaje) {
        Soporte ticket = new Soporte();
        ticket.setNombre(nombre.trim());
        ticket.setCorreo(correo.trim());
        ticket.setAsunto(asunto.trim());
        ticket.setMensaje(mensaje.trim());
        ticket.setEstado(Soporte.ESTADO_PENDIENTE);
        Soporte guardado = soporteRepository.save(ticket);

        // Mejor esfuerzo: un fallo del correo no debe perder el ticket
        notificacionesClient.notificarContacto(
                guardado.getNombre(), guardado.getCorreo(), guardado.getAsunto(), guardado.getMensaje());
        return guardado;
    }

    public List<Soporte> listar(String estado) {
        if (estado != null && !estado.isBlank()) {
            return soporteRepository.findByEstadoOrderByFechaCreacionDesc(estado.trim());
        }
        return soporteRepository.findAllByOrderByFechaCreacionDesc();
    }

    public List<Soporte> listarPorCorreo(String correo) {
        return soporteRepository.findByCorreoIgnoreCaseOrderByFechaCreacionDesc(correo.trim());
    }

    public Optional<Soporte> buscarPorId(Integer id) {
        return soporteRepository.findById(id);
    }

    @Transactional
    public Soporte cambiarEstado(Integer id, String estado) {
        if (estado == null || !ESTADOS_VALIDOS.contains(estado.trim())) {
            throw new IllegalArgumentException("Estado inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
        Soporte ticket = soporteRepository.findById(id)
                .orElseThrow(() -> new TicketNoEncontradoException("Ticket no encontrado"));
        ticket.setEstado(estado.trim());
        return soporteRepository.save(ticket);
    }

    public static class TicketNoEncontradoException extends RuntimeException {
        public TicketNoEncontradoException(String message) {
            super(message);
        }
    }
}
