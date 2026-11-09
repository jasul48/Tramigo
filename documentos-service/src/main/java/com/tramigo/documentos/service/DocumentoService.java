package com.tramigo.documentos.service;

import com.tramigo.documentos.model.Documento;
import com.tramigo.documentos.repository.DocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final FileStorageService fileStorageService;

    public DocumentoService(DocumentoRepository documentoRepository, FileStorageService fileStorageService) {
        this.documentoRepository = documentoRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Documento> listarPorUsuario(String usuarioId) {
        return documentoRepository.findByUsuarioIdOrderByFechaSubidaDesc(usuarioId);
    }

    public Optional<Documento> buscar(Integer documentoId, String usuarioId) {
        return documentoRepository.findByDocumentoIdAndUsuarioId(documentoId, usuarioId);
    }

    @Transactional
    public Documento subir(String usuarioId, MultipartFile archivo, String nombrePersonalizado) throws IOException {
        if (archivo == null || archivo.isEmpty() || !fileStorageService.isAllowedDoc(archivo.getOriginalFilename())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido o archivo no seleccionado.");
        }

        String nombre = (nombrePersonalizado != null && !nombrePersonalizado.isBlank())
                ? nombrePersonalizado.trim()
                : archivo.getOriginalFilename();
        if (nombre != null && nombre.length() > 100) {
            nombre = nombre.substring(0, 100);
        }

        FileStorageService.StoredDocResult stored = fileStorageService.storeDocument(archivo, usuarioId);
        try {
            Documento doc = new Documento();
            doc.setUsuarioId(usuarioId);
            doc.setNombrePersonalizado(nombre);
            doc.setNombreArchivo(stored.getFilename());
            doc.setTipoArchivo(stored.getExtension());
            return documentoRepository.save(doc);
        } catch (RuntimeException e) {
            fileStorageService.deleteDocumentFile(stored.getFilename());
            throw e;
        }
    }

    /** @return true si se eliminó; false si no existe o no pertenece al usuario. */
    @Transactional
    public boolean eliminar(Integer documentoId, String usuarioId) {
        Optional<Documento> docOpt = documentoRepository.findByDocumentoIdAndUsuarioId(documentoId, usuarioId);
        if (docOpt.isEmpty()) {
            return false;
        }
        Documento doc = docOpt.get();
        fileStorageService.deleteDocumentFile(doc.getNombreArchivo());
        documentoRepository.delete(doc);
        return true;
    }
}
