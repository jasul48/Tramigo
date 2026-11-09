package com.tramigo.seguimiento.controller;

import com.tramigo.seguimiento.dto.ActualizarEstadoRequest;
import com.tramigo.seguimiento.dto.CrearSolicitudRequest;
import com.tramigo.seguimiento.model.Solicitud;
import com.tramigo.seguimiento.service.SeguimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seguimiento")
public class SeguimientoRestController {
    private final SeguimientoService service;
    public SeguimientoRestController(SeguimientoService service) { this.service = service; }

    @PostMapping("/solicitudes")
    public ResponseEntity<?> crear(@RequestBody CrearSolicitudRequest request) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }
    @GetMapping("/solicitudes")
    public List<Solicitud> listar(@RequestParam(required = false) Integer usuarioId) { return service.listar(usuarioId); }
    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.obtener(id)); }
        catch (IllegalArgumentException e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage())); }
    }
    @PutMapping("/solicitudes/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoRequest request) {
        try { return ResponseEntity.ok(service.actualizarEstado(id, request)); }
        catch (IllegalArgumentException e) {
            HttpStatus status = e.getMessage().startsWith("Solicitud no encontrada") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    @GetMapping("/solicitudes/{id}/historial")
    public ResponseEntity<?> historial(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.obtener(id).getHistorial()); }
        catch (IllegalArgumentException e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage())); }
    }
}
