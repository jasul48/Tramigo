package com.tramigo.usuarios.controller;

import com.tramigo.usuarios.model.Persona;
import com.tramigo.usuarios.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/personas")
public class PersonaRestController {

    @Autowired
    private PersonaRepository personaRepository;

    @GetMapping
    public ResponseEntity<List<Persona>> listar(@RequestParam(value = "q", required = false) String q) {
        List<Persona> personas;
        if (q != null && !q.trim().isEmpty()) {
            String cleanQ = q.trim();
            if (cleanQ.matches("\\d+")) {
                personas = personaRepository.searchByIdOrNombre(Integer.parseInt(cleanQ), cleanQ);
            } else {
                personas = personaRepository.searchByNombre(cleanQ);
            }
        } else {
            personas = personaRepository.findAllByOrderByIdAsc();
        }
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer id) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Persona no encontrada con ID: " + id
            ));
        }
        return ResponseEntity.ok(personaOpt.get());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Persona persona) {
        if (persona.getId() == null || persona.getNombre() == null || persona.getEdad() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "id, nombre y edad son obligatorios"
            ));
        }

        if (personaRepository.existsById(persona.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", "El ID " + persona.getId() + " ya existe"
            ));
        }

        persona.setNombre(persona.getNombre().trim());
        Persona guardada = personaRepository.save(persona);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Integer id, @RequestBody Persona persona) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Persona no encontrada con ID: " + id
            ));
        }

        Persona existente = personaOpt.get();
        if (persona.getNombre() != null) {
            existente.setNombre(persona.getNombre().trim());
        }
        if (persona.getEdad() != null) {
            existente.setEdad(persona.getEdad());
        }
        Persona actualizada = personaRepository.save(existente);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable("id") Integer id) {
        if (!personaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Persona no encontrada con ID: " + id
            ));
        }
        personaRepository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Persona eliminada correctamente con ID: " + id
        ));
    }
}
