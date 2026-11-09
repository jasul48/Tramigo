package com.tramigo.notificaciones.controller;

import com.tramigo.notificaciones.dto.ContactoRequest;
import com.tramigo.notificaciones.dto.NotificacionDTO;
import com.tramigo.notificaciones.model.Notificacion;
import com.tramigo.notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio interno: lo consumen otros microservicios (p. ej. soporte-service) via Eureka.
 * No se expone por el api-gateway.
 */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionRestController {

    @Autowired
    private NotificacionService notificacionService;

    @PostMapping("/contacto")
    public ResponseEntity<?> contacto(@RequestBody ContactoRequest request) {
        if (esVacio(request.getNombre()) || esVacio(request.getCorreo())
                || esVacio(request.getAsunto()) || esVacio(request.getMensaje())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Nombre, correo, asunto y mensaje son obligatorios"
            ));
        }

        Notificacion n = notificacionService.enviarContacto(
                request.getNombre().trim(), request.getCorreo().trim(),
                request.getAsunto().trim(), request.getMensaje().trim());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "success", true,
                "message", "Notificación procesada",
                "notificacion", NotificacionDTO.fromEntity(n)
        ));
    }

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> listar(@RequestParam(value = "estado", required = false) String estado) {
        List<NotificacionDTO> lista = notificacionService.listar(estado).stream()
                .map(NotificacionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer id) {
        Optional<Notificacion> opt = notificacionService.buscarPorId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Notificación no encontrada con ID: " + id
            ));
        }
        return ResponseEntity.ok(NotificacionDTO.fromEntity(opt.get()));
    }

    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}
