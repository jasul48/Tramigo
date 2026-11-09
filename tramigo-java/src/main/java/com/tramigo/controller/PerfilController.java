package com.tramigo.controller;

import com.tramigo.model.Usuario;
import com.tramigo.service.FileStorageService;
import com.tramigo.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/perfil")
    public String perfilPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String usuarioId = (String) session.getAttribute("usuario_id");
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para ver tu perfil.");
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/index";
        }

        model.addAttribute("usuario", usuarioOpt.get());
        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam("nombre") String nombre,
                                   @RequestParam("apellido") String apellido,
                                   @RequestParam(value = "telefono", required = false) String telefono,
                                   @RequestParam(value = "foto_perfil", required = false) MultipartFile fotoPerfil,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String usuarioId = (String) session.getAttribute("usuario_id");
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para actualizar tu perfil.");
            return "redirect:/login";
        }

        try {
            String fotoNombre = null;
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                if (fileStorageService.isAllowedImage(fotoPerfil.getOriginalFilename())) {
                    fotoNombre = fileStorageService.storeProfilePhoto(fotoPerfil, usuarioId);
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Formato de imagen no permitido.");
                    return "redirect:/perfil";
                }
            }

            usuarioService.actualizarPerfil(usuarioId, nombre, apellido, telefono, fotoNombre);
            session.setAttribute("nombre", nombre.trim());
            if (fotoNombre != null) {
                session.setAttribute("foto_perfil", fotoNombre);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar perfil: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    @PostMapping("/cambiar_password")
    public String cambiarPassword(@RequestParam("old_password") String oldPass,
                                  @RequestParam("new_password") String newPass,
                                  @RequestParam("confirm_password") String confirmPass,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        String usuarioId = (String) session.getAttribute("usuario_id");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (!newPass.equals(confirmPass)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Las nuevas contraseñas no coinciden.");
            return "redirect:/perfil";
        }

        boolean actualizado = usuarioService.cambiarPassword(usuarioId, oldPass, newPass);
        if (actualizado) {
            redirectAttributes.addFlashAttribute("successMessage", "Contraseña actualizada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "La contraseña actual es incorrecta.");
        }

        return "redirect:/perfil";
    }
}
