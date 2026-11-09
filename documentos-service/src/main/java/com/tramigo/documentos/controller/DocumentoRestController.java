package com.tramigo.documentos.controller;

import com.tramigo.documentos.client.UsuariosClient;
import com.tramigo.documentos.dto.DocumentoDTO;
import com.tramigo.documentos.model.Documento;
import com.tramigo.documentos.service.DocumentoService;
import com.tramigo.documentos.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoRestController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UsuariosClient usuariosClient;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DocumentoDTO>> listarPorUsuario(@PathVariable("usuarioId") String usuarioId) {
        List<DocumentoDTO> lista = documentoService.listarPorUsuario(usuarioId).stream()
                .map(DocumentoDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer id,
                                         @RequestParam("usuarioId") String usuarioId) {
        Optional<Documento> docOpt = documentoService.buscar(id, usuarioId);
        if (docOpt.isEmpty()) {
            return noEncontrado();
        }
        return ResponseEntity.ok(DocumentoDTO.fromEntity(docOpt.get()));
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<?> descargar(@PathVariable("id") Integer id,
                                       @RequestParam("usuarioId") String usuarioId) {
        Optional<Documento> docOpt = documentoService.buscar(id, usuarioId);
        if (docOpt.isEmpty() || !fileStorageService.existe(docOpt.get().getNombreArchivo())) {
            return noEncontrado();
        }

        try {
            Documento doc = docOpt.get();
            Path ruta = fileStorageService.resolverSeguro(doc.getNombreArchivo());
            Resource recurso = new UrlResource(ruta.toUri());

            String contentType = Files.probeContentType(ruta);
            MediaType mediaType = contentType != null
                    ? MediaType.parseMediaType(contentType)
                    : MediaType.APPLICATION_OCTET_STREAM;

            String nombreDescarga = doc.getNombrePersonalizado();
            String sufijo = "." + doc.getTipoArchivo();
            if (!nombreDescarga.toLowerCase().endsWith(sufijo)) {
                nombreDescarga += sufijo;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline().filename(nombreDescarga, StandardCharsets.UTF_8).build().toString())
                    .body(recurso);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al leer el documento: " + e.getMessage()
            ));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subir(@RequestParam("usuarioId") String usuarioId,
                                   @RequestParam("archivo") MultipartFile archivo,
                                   @RequestParam(value = "nombre", required = false) String nombre) {
        if (usuarioId == null || usuarioId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "usuarioId es requerido"
            ));
        }

        try {
            if (!usuariosClient.existeUsuario(usuarioId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Usuario no encontrado"
                ));
            }

            Documento doc = documentoService.subir(usuarioId, archivo, nombre);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Documento subido correctamente",
                    "documento", DocumentoDTO.fromEntity(doc)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (UsuariosClient.UsuariosNoDisponibleException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al guardar documento: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable("id") Integer id,
                                      @RequestParam("usuarioId") String usuarioId) {
        if (!documentoService.eliminar(id, usuarioId)) {
            return noEncontrado();
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Documento eliminado"
        ));
    }

    private ResponseEntity<?> noEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", "Documento no encontrado o no tienes permiso para acceder a él"
        ));
    }
}
