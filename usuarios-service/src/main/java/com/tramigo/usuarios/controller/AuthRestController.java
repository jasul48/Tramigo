package com.tramigo.usuarios.controller;

import com.tramigo.usuarios.dto.LoginRequest;
import com.tramigo.usuarios.dto.RegistroRequest;
import com.tramigo.usuarios.dto.UsuarioDTO;
import com.tramigo.usuarios.model.Usuario;
import com.tramigo.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getCorreo() == null || request.getContrasena() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Correo y contraseña son requeridos"
            ));
        }

        Optional<Usuario> usuarioOpt = usuarioService.autenticar(request.getCorreo(), request.getContrasena());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Autenticación exitosa",
                    "usuario", UsuarioDTO.fromEntity(usuario)
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Correo o contraseña incorrectos"
            ));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        if (request.getCorreo() == null || request.getContrasena() == null ||
            request.getNombre() == null || request.getApellido() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Nombre, apellido, correo y contraseña son obligatorios"
            ));
        }

        try {
            if (usuarioService.existeCorreo(request.getCorreo())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "success", false,
                        "message", "Ese correo ya está registrado"
                ));
            }

            Usuario usuario = usuarioService.registrarUsuario(
                    request.getNombre(),
                    request.getApellido(),
                    request.getTelefono(),
                    request.getCorreo(),
                    request.getContrasena()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Usuario registrado exitosamente",
                    "usuario", UsuarioDTO.fromEntity(usuario)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al registrar usuario: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/existe-correo")
    public ResponseEntity<?> existeCorreo(@RequestParam("correo") String correo) {
        boolean existe = usuarioService.existeCorreo(correo);
        return ResponseEntity.ok(Map.of(
                "correo", correo,
                "existe", existe
        ));
    }
}
