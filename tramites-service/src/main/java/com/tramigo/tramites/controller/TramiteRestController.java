package com.tramigo.tramites.controller;

import com.tramigo.tramites.model.Tramite;
import com.tramigo.tramites.service.TramiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tramites")
public class TramiteRestController {

    @Autowired
    private TramiteService tramiteService;

    /**
     * Catálogo. Filtros opcionales: ?categoria=Viajes &q=pasaporte &grupo=cuenta-bancaria
     * Sin "grupo" devuelve solo los trámites de primer nivel.
     */
    @GetMapping
    public ResponseEntity<List<Tramite>> listar(@RequestParam(value = "categoria", required = false) String categoria,
                                                @RequestParam(value = "q", required = false) String q,
                                                @RequestParam(value = "grupo", required = false) String grupo) {
        return ResponseEntity.ok(tramiteService.listar(categoria, q, grupo));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> categorias() {
        return ResponseEntity.ok(tramiteService.categorias());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> buscarPorSlug(@PathVariable("slug") String slug) {
        Optional<Tramite> opt = tramiteService.buscarPorSlug(slug);
        if (opt.isEmpty()) {
            return noEncontrado(slug);
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Tramite tramite) {
        if (esVacio(tramite.getSlug()) || esVacio(tramite.getNombre()) || esVacio(tramite.getCategoria())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "slug, nombre y categoria son obligatorios"
            ));
        }

        try {
            Tramite creado = tramiteService.crear(tramite);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
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

    @PutMapping("/{slug}")
    public ResponseEntity<?> actualizar(@PathVariable("slug") String slug, @RequestBody Tramite tramite) {
        try {
            return ResponseEntity.ok(tramiteService.actualizar(slug, tramite));
        } catch (IllegalArgumentException e) {
            return noEncontrado(slug);
        }
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<?> eliminar(@PathVariable("slug") String slug) {
        if (!tramiteService.eliminar(slug)) {
            return noEncontrado(slug);
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Trámite eliminado: " + slug
        ));
    }

    private ResponseEntity<?> noEncontrado(String slug) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", "Trámite no encontrado: " + slug
        ));
    }

    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}
