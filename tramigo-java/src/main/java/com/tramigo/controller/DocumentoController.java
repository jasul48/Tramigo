package com.tramigo.controller;

import com.tramigo.model.Documento;
import com.tramigo.repository.DocumentoRepository;
import com.tramigo.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class DocumentoController {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/mis_documentos")
    public String misDocumentos(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String usuarioId = (String) session.getAttribute("usuario_id");
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para acceder a tus documentos.");
            return "redirect:/login";
        }

        List<Documento> documentos = documentoRepository.findByUsuarioIdOrderByFechaSubidaDesc(usuarioId);
        model.addAttribute("documentos", documentos);
        return "documentos";
    }

    @PostMapping("/mis_documentos")
    public String subirDocumento(@RequestParam("archivo_doc") MultipartFile archivo,
                                 @RequestParam(value = "nombre_doc", required = false) String nombreDoc,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String usuarioId = (String) session.getAttribute("usuario_id");
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para subir documentos.");
            return "redirect:/login";
        }

        if (archivo == null || archivo.isEmpty() || !fileStorageService.isAllowedDoc(archivo.getOriginalFilename())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tipo de archivo no permitido o archivo no seleccionado.");
            return "redirect:/mis_documentos";
        }

        try {
            String nombrePersonalizado = (nombreDoc != null && !nombreDoc.trim().isEmpty())
                    ? nombreDoc.trim()
                    : archivo.getOriginalFilename();

            FileStorageService.StoredDocResult result = fileStorageService.storeDocument(archivo, usuarioId);

            Documento doc = new Documento();
            doc.setUsuarioId(usuarioId);
            doc.setNombrePersonalizado(nombrePersonalizado);
            doc.setNombreArchivo(result.getFilename());
            doc.setTipoArchivo(result.getExtension());

            documentoRepository.save(doc);
            redirectAttributes.addFlashAttribute("successMessage", "Documento subido correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar documento: " + e.getMessage());
        }

        return "redirect:/mis_documentos";
    }

    @GetMapping("/eliminar_documento/{docId}")
    public String eliminarDocumento(@PathVariable("docId") Integer docId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String usuarioId = (String) session.getAttribute("usuario_id");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<Documento> docOpt = documentoRepository.findById(docId);
        if (docOpt.isPresent() && docOpt.get().getUsuarioId().equals(usuarioId)) {
            Documento doc = docOpt.get();
            fileStorageService.deleteDocumentFile(doc.getNombreArchivo());
            documentoRepository.delete(doc);
            redirectAttributes.addFlashAttribute("successMessage", "Documento eliminado.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Documento no encontrado o no tienes permiso para eliminarlo.");
        }

        return "redirect:/mis_documentos";
    }
}
