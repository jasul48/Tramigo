package com.tramigo.soporte.controller;

import com.tramigo.soporte.dto.EstadoRequest;
import com.tramigo.soporte.dto.TicketDTO;
import com.tramigo.soporte.dto.TicketRequest;
import com.tramigo.soporte.model.Soporte;
import com.tramigo.soporte.service.SoporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/soporte")
public class SoporteRestController {

    @Autowired
    private SoporteService soporteService;

    /** Equivalente a POST /contacto del monolito. */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TicketRequest request) {
        if (esVacio(request.getNombre()) || esVacio(request.getCorreo())
                || esVacio(request.getAsunto()) || esVacio(request.getMensaje())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Nombre, correo, asunto y mensaje son obligatorios"
            ));
        }

        try {
            Soporte ticket = soporteService.crearTicket(
                    request.getNombre(), request.getCorreo(), request.getAsunto(), request.getMensaje());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "¡Tu mensaje fue recibido! Nuestro equipo lo revisará y te contactará por correo.",
                    "ticket", TicketDTO.fromEntity(ticket)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al guardar el mensaje: " + e.getMessage()
            ));
        }
    }

    /** Equivalente a GET /admin/mensajes del monolito. Filtro opcional: ?estado=Pendiente */
    @GetMapping
    public ResponseEntity<List<TicketDTO>> listar(@RequestParam(value = "estado", required = false) String estado) {
        List<TicketDTO> lista = soporteService.listar(estado).stream()
                .map(TicketDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/correo")
    public ResponseEntity<List<TicketDTO>> listarPorCorreo(@RequestParam("correo") String correo) {
        List<TicketDTO> lista = soporteService.listarPorCorreo(correo).stream()
                .map(TicketDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer id) {
        Optional<Soporte> ticketOpt = soporteService.buscarPorId(id);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Ticket no encontrado con ID: " + id
            ));
        }
        return ResponseEntity.ok(TicketDTO.fromEntity(ticketOpt.get()));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable("id") Integer id,
                                           @RequestBody EstadoRequest request) {
        try {
            Soporte actualizado = soporteService.cambiarEstado(id, request.getEstado());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Estado actualizado",
                    "ticket", TicketDTO.fromEntity(actualizado)
            ));
        } catch (SoporteService.TicketNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}
