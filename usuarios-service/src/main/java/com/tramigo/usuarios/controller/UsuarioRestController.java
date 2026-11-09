package com.tramigo.usuarios.controller;

import com.tramigo.usuarios.dto.ActualizarPerfilRequest;
import com.tramigo.usuarios.dto.PasswordChangeRequest;
import com.tramigo.usuarios.dto.UsuarioDTO;
import com.tramigo.usuarios.model.Usuario;
import com.tramigo.usuarios.repository.UsuarioRepository;
import com.tramigo.usuarios.service.FileStorageService;
import com.tramigo.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> lista = usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") String id) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Usuario no encontrado"
            ));
        }
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuarioOpt.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable("id") String id,
                                             @RequestBody ActualizarPerfilRequest request) {
        try {
            usuarioService.actualizarPerfil(id, request.getNombre(), request.getApellido(),
                    request.getTelefono(), request.getFotoPerfil());
            Optional<Usuario> actualizado = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Perfil actualizado con éxito",
                    "usuario", UsuarioDTO.fromEntity(actualizado.orElse(null))
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al actualizar perfil: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> cambiarPassword(@PathVariable("id") String id,
                                            @RequestBody PasswordChangeRequest request) {
        if (request.getOldPassword() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "oldPassword y newPassword son requeridos"
            ));
        }

        try {
            boolean cambiado = usuarioService.cambiarPassword(id, request.getOldPassword(), request.getNewPassword());
            if (cambiado) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Contraseña actualizada correctamente"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "La contraseña actual es incorrecta"
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFotoPerfil(@PathVariable("id") String id,
                                            @RequestParam("foto") MultipartFile file) {
        try {
            if (file.isEmpty() || !fileStorageService.isAllowedImage(file.getOriginalFilename())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Archivo vacío o formato no permitido (png, jpg, jpeg, gif)"
                ));
            }

            String storedFileName = fileStorageService.storeProfilePhoto(file, id);
            Optional<Usuario> userOpt = usuarioService.buscarPorId(id);
            if (userOpt.isPresent()) {
                Usuario user = userOpt.get();
                usuarioService.actualizarPerfil(id, user.getNombre(), user.getApellido(), user.getTelefono(), storedFileName);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Foto de perfil subida exitosamente",
                    "fotoPerfil", storedFileName,
                    "url", "/uploads/perfiles/" + storedFileName
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al subir foto: " + e.getMessage()
            ));
        }
    }
}
