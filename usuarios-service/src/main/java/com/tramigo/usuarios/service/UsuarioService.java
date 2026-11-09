package com.tramigo.usuarios.service;

import com.tramigo.usuarios.model.Usuario;
import com.tramigo.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo.trim());
    }

    @Transactional
    public Usuario registrarUsuario(String nombre, String apellido, String telefono, String correo, String contrasena) {
        String correoNormalizado = correo.trim();
        if (usuarioRepository.existsByCorreo(correoNormalizado)) {
            throw new IllegalArgumentException("Ese correo ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setTelefono(telefono == null ? "" : telefono.trim());
        usuario.setCorreo(correoNormalizado);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Optional<Usuario> autenticar(String correo, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo.trim());
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();
        String hash = usuario.getContrasena();
        boolean coincide;
        if (hash != null && hash.matches("^\\$2[aby]\\$.*")) {
            coincide = passwordEncoder.matches(contrasena, hash);
        } else {
            coincide = contrasena.equals(hash);
            if (coincide) {
                usuario.setContrasena(passwordEncoder.encode(contrasena));
                usuarioRepository.save(usuario);
            }
        }
        return coincide ? Optional.of(usuario) : Optional.empty();
    }

    public Optional<Usuario> buscarPorId(String usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    @Transactional
    public void actualizarPerfil(String usuarioId, String nombre, String apellido, String telefono, String fotoPerfil) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        if (telefono != null) {
            usuario.setTelefono(telefono.trim());
        }
        if (fotoPerfil != null && !fotoPerfil.isBlank()) {
            usuario.setFotoPerfil(fotoPerfil);
        }
        usuarioRepository.save(usuario);
    }

    @Transactional
    public boolean cambiarPassword(String usuarioId, String oldPass, String newPass) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        String hash = usuario.getContrasena();
        boolean coincide = hash != null && hash.matches("^\\$2[aby]\\$.*")
                ? passwordEncoder.matches(oldPass, hash)
                : oldPass.equals(hash);
        if (!coincide) {
            return false;
        }

        usuario.setContrasena(passwordEncoder.encode(newPass));
        usuarioRepository.save(usuario);
        return true;
    }
}
