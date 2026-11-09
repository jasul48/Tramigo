package com.tramigo.controller;

import com.tramigo.model.Soporte;
import com.tramigo.service.SoporteApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SoporteController {

    private final SoporteApiService soporteApiService;

    public SoporteController(SoporteApiService soporteApiService) {
        this.soporteApiService = soporteApiService;
    }

    @GetMapping("/contacto")
    public String contactoPage() {
        return "contacto";
    }

    @PostMapping("/contacto")
    public String contacto(@RequestParam("nombre") String nombre,
                           @RequestParam("correo") String correo,
                           @RequestParam("asunto") String asunto,
                           @RequestParam("mensaje") String mensaje,
                           RedirectAttributes redirectAttributes) {
        if (vacio(nombre) || vacio(correo) || vacio(asunto) || vacio(mensaje)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Completa todos los campos para enviar tu mensaje.");
            return "redirect:/contacto";
        }

        try {
            soporteApiService.crearTicket(nombre, correo, asunto, mensaje);
            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Tu mensaje fue recibido! Nuestro equipo lo revisará y te contactará por correo.");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No pudimos enviar tu mensaje en este momento. Verifica que los microservicios estén activos e inténtalo de nuevo.");
        }
        return "redirect:/contacto";
    }

    @GetMapping("/admin/mensajes")
    public String verMensajes(Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Soporte> mensajes = soporteApiService.listarTickets();
            model.addAttribute("mensajes", mensajes);
            return "admin_mensajes";
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No se pudieron consultar los mensajes. Verifica que los microservicios estén activos.");
            return "redirect:/contacto";
        }
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
