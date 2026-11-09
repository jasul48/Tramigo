package com.tramigo.controller;

import com.tramigo.model.Usuario;
import com.tramigo.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("correo") String correo,
                        @RequestParam("contrasena") String contrasena,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.autenticar(correo, contrasena);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuario_id", usuario.getUsuarioId());
            session.setAttribute("nombre", usuario.getNombre());
            session.setAttribute("foto_perfil", usuario.getFotoPerfil());
            redirectAttributes.addFlashAttribute("successMessage", "¡Bienvenido de nuevo, " + usuario.getNombre() + "!");
            return "redirect:/index";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Correo o contraseña incorrectos.");
            return "redirect:/login";
        }
    }

    @GetMapping("/registro")
    public String registroPage() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam("nombre") String nombre,
                           @RequestParam("apellido") String apellido,
                           @RequestParam(value = "telefono", required = false) String telefono,
                           @RequestParam("correo") String correo,
                           @RequestParam("contrasena") String contrasena,
                           RedirectAttributes redirectAttributes) {
        try {
            if (usuarioService.existeCorreo(correo)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ese correo ya está registrado.");
                return "redirect:/registro";
            }
            usuarioService.registrarUsuario(nombre, apellido, telefono, correo, contrasena);
            redirectAttributes.addFlashAttribute("successMessage", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Has cerrado sesión correctamente.");
        return "redirect:/index";
    }
}
