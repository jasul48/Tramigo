package com.tramigo.documentos.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${tramigo.upload.docs:uploads/documentos}")
    private String docsUploadDir;

    private static final List<String> ALLOWED_DOC_EXTENSIONS = List.of("pdf", "png", "jpg", "jpeg", "doc", "docx");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(docsUploadDir));
        } catch (IOException e) {
            System.err.println("No se pudo crear la carpeta de documentos: " + e.getMessage());
        }
    }

    public boolean isAllowedDoc(String filename) {
        String ext = extensionDe(filename);
        return ext != null && ALLOWED_DOC_EXTENSIONS.contains(ext);
    }

    public StoredDocResult storeDocument(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty() || !isAllowedDoc(file.getOriginalFilename())) {
            throw new IllegalArgumentException("Formato de documento no permitido o archivo vacío.");
        }
        String ext = extensionDe(file.getOriginalFilename());
        String userSeguro = userId.replaceAll("[^a-zA-Z0-9-]", "_");
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String filename = "doc_" + userSeguro + "_" + randomSuffix + "." + ext;

        Path targetLocation = resolverSeguro(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }
        return new StoredDocResult(filename, ext);
    }

    /** Ruta absoluta del archivo, garantizando que no salga de la carpeta de documentos. */
    public Path resolverSeguro(String filename) {
        Path base = Paths.get(docsUploadDir).toAbsolutePath().normalize();
        Path target = base.resolve(filename).normalize();
        if (!target.startsWith(base)) {
            throw new IllegalArgumentException("Nombre de archivo inválido.");
        }
        return target;
    }

    public boolean existe(String filename) {
        return Files.isRegularFile(resolverSeguro(filename));
    }

    public void deleteDocumentFile(String filename) {
        try {
            Files.deleteIfExists(resolverSeguro(filename));
        } catch (IOException | IllegalArgumentException ignored) {
        }
    }

    private static String extensionDe(String filename) {
        if (filename == null) return null;
        String cleaned = StringUtils.getFilename(StringUtils.cleanPath(filename));
        String ext = cleaned == null ? null : StringUtils.getFilenameExtension(cleaned);
        return ext == null || ext.isBlank() ? null : ext.toLowerCase(Locale.ROOT);
    }

    public static class StoredDocResult {
        private final String filename;
        private final String extension;

        public StoredDocResult(String filename, String extension) {
            this.filename = filename;
            this.extension = extension;
        }

        public String getFilename() {
            return filename;
        }

        public String getExtension() {
            return extension;
        }
    }
}
