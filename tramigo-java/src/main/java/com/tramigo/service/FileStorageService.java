package com.tramigo.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${tramigo.upload.profiles:uploads/perfiles}")
    private String profilesUploadDir;

    @Value("${tramigo.upload.docs:uploads/documentos}")
    private String docsUploadDir;

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg", "gif");
    private static final List<String> ALLOWED_DOC_EXTENSIONS = Arrays.asList("pdf", "png", "jpg", "jpeg", "doc", "docx");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(profilesUploadDir));
            Files.createDirectories(Paths.get(docsUploadDir));
        } catch (IOException e) {
            System.err.println("No se pudieron crear las carpetas de carga: " + e.getMessage());
        }
    }

    public boolean isAllowedImage(String filename) {
        if (filename == null || !filename.contains(".")) return false;
        String ext = FilenameUtils.getExtension(filename).toLowerCase();
        return ALLOWED_IMAGE_EXTENSIONS.contains(ext);
    }

    public boolean isAllowedDoc(String filename) {
        if (filename == null || !filename.contains(".")) return false;
        String ext = FilenameUtils.getExtension(filename).toLowerCase();
        return ALLOWED_DOC_EXTENSIONS.contains(ext);
    }

    public String storeProfilePhoto(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty() || !isAllowedImage(file.getOriginalFilename())) {
            throw new IllegalArgumentException("Formato de imagen no permitido o archivo vacío.");
        }
        String originalName = FilenameUtils.getName(file.getOriginalFilename());
        String cleanOriginalName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String filename = userId + "_" + cleanOriginalName;

        Path targetLocation = Paths.get(profilesUploadDir).resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public StoredDocResult storeDocument(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty() || !isAllowedDoc(file.getOriginalFilename())) {
            throw new IllegalArgumentException("Formato de documento no permitido o archivo vacío.");
        }
        String originalName = FilenameUtils.getName(file.getOriginalFilename());
        String ext = FilenameUtils.getExtension(originalName).toLowerCase();
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String filename = "doc_" + userId + "_" + randomSuffix + "." + ext;

        Path targetLocation = Paths.get(docsUploadDir).resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return new StoredDocResult(filename, ext);
    }

    public void deleteDocumentFile(String filename) {
        try {
            Path file = Paths.get(docsUploadDir).resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
        }
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
