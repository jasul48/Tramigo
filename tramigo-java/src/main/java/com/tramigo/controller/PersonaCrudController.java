package com.tramigo.controller;

import com.tramigo.model.Persona;
import com.tramigo.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class PersonaCrudController {

    @Autowired
    private PersonaRepository personaRepository;

    @GetMapping("/crud")
    public String crud(@RequestParam(value = "q", required = false) String q, Model model) {
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
        model.addAttribute("personas", personas);
        model.addAttribute("query", q != null ? q : "");
        return "crud";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam("id") Integer id,
                          @RequestParam("nombre") String nombre,
                          @RequestParam("edad") Integer edad,
                          RedirectAttributes redirectAttributes) {
        try {
            if (personaRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: El ID " + id + " ya existe en el sistema");
                return "redirect:/crud";
            }
            Persona persona = new Persona(id, nombre.trim(), edad);
            personaRepository.save(persona);
            redirectAttributes.addFlashAttribute("successMessage", "¡Persona " + nombre + " agregada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/crud";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Persona no encontrada");
            return "redirect:/crud";
        }
        model.addAttribute("persona", personaOpt.get());
        return "editar";
    }

    @PostMapping("/editar/{id}")
    public String editarGuardar(@PathVariable("id") Integer id,
                                @RequestParam("nombre") String nombre,
                                @RequestParam("edad") Integer edad,
                                RedirectAttributes redirectAttributes) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isPresent()) {
            Persona persona = personaOpt.get();
            persona.setNombre(nombre.trim());
            persona.setEdad(edad);
            personaRepository.save(persona);
            redirectAttributes.addFlashAttribute("successMessage", "Datos actualizados correctamente");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Persona no encontrada");
        }
        return "redirect:/crud";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        if (personaRepository.existsById(id)) {
            personaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Persona eliminada correctamente");
        }
        return "redirect:/crud";
    }
}
