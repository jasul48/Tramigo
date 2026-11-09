package com.tramigo.seguimiento.service;

import com.tramigo.seguimiento.dto.ActualizarEstadoRequest;
import com.tramigo.seguimiento.dto.CrearSolicitudRequest;
import com.tramigo.seguimiento.model.Solicitud;
import com.tramigo.seguimiento.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class SeguimientoService {
    private static final Set<String> ESTADOS = Set.of("RECIBIDA", "EN_REVISION", "EN_PROCESO", "PENDIENTE_DOCUMENTOS", "APROBADA", "RECHAZADA", "FINALIZADA", "CANCELADA");
    private final SolicitudRepository repository;
    public SeguimientoService(SolicitudRepository repository) { this.repository = repository; }

    @Transactional
    public Solicitud crear(CrearSolicitudRequest request) {
        if (request.usuarioId() == null || request.usuarioId() <= 0) throw new IllegalArgumentException("usuarioId debe ser un número positivo");
        if (request.titulo() == null || request.titulo().isBlank()) throw new IllegalArgumentException("titulo es obligatorio");
        Solicitud s = new Solicitud();
        s.setUsuarioId(request.usuarioId()); s.setTramiteId(request.tramiteId());
        s.setTitulo(request.titulo().trim()); s.setDescripcion(request.descripcion()); s.setEstado("RECIBIDA");
        s.agregarEvento("RECIBIDA", "Solicitud registrada");
        return repository.save(s);
    }
    public List<Solicitud> listar(Integer usuarioId) {
        return usuarioId == null ? repository.findAll() : repository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }
    public Solicitud obtener(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada: " + id)); }
    @Transactional
    public Solicitud actualizarEstado(Long id, ActualizarEstadoRequest request) {
        Solicitud s = obtener(id);
        if (request.estado() == null || request.estado().isBlank()) throw new IllegalArgumentException("estado es obligatorio");
        String nuevo = request.estado().trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        if (!ESTADOS.contains(nuevo)) throw new IllegalArgumentException("Estado no válido. Usa: " + String.join(", ", ESTADOS));
        s.setEstado(nuevo); s.agregarEvento(nuevo, request.comentario());
        return repository.save(s);
    }
}
