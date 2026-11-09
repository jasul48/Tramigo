package com.tramigo.service;

import com.tramigo.model.Usuario;
import com.tramigo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Transactional
    public Usuario registrarUsuario(String nombre, String apellido, String telefono, String correo, String contrasena) {
        if (existeCorreo(correo)) {
            throw new IllegalArgumentException("Ese correo ya está registrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setTelefono(telefono != null ? telefono.trim() : "");
        usuario.setCorreo(correo.trim());
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setFotoPerfil("default_profile.png");

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> autenticar(String correo, String contrasenaPlana) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo.trim());
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();
        String storedHash = usuario.getContrasena();

        // Verificamos si coincide con BCrypt
        boolean matches = false;
        try {
            matches = passwordEncoder.matches(contrasenaPlana, storedHash);
        } catch (Exception e) {
            matches = false;
        }

        // Fallback: si venía de texto plano o formato simple en BD previa
        if (!matches && (contrasenaPlana.equals(storedHash) || storedHash.startsWith("scrypt:") || storedHash.startsWith("pbkdf2:"))) {
            // Si era igual en texto plano o hash antiguo y se desea migrar
            if (contrasenaPlana.equals(storedHash)) {
                matches = true;
                usuario.setContrasena(passwordEncoder.encode(contrasenaPlana));
                usuarioRepository.save(usuario);
            }
        }

        return matches ? Optional.of(usuario) : Optional.empty();
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
        if (fotoPerfil != null && !fotoPerfil.trim().isEmpty()) {
            usuario.setFotoPerfil(fotoPerfil);
        }
        usuarioRepository.save(usuario);
    }

    @Transactional
    public boolean cambiarPassword(String usuarioId, String oldPass, String newPass) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        boolean currentMatches = false;
        try {
            currentMatches = passwordEncoder.matches(oldPass, usuario.getContrasena());
        } catch (Exception ignored) {
        }
        if (!currentMatches && oldPass.equals(usuario.getContrasena())) {
            currentMatches = true;
        }

        if (!currentMatches) {
            return false;
        }

        usuario.setContrasena(passwordEncoder.encode(newPass));
        usuarioRepository.save(usuario);
        return true;
    }
}
