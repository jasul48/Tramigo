package com.tramigo.usuarios.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif");

    @Value("${tramigo.upload.profiles:uploads/perfiles}")
    private String profilesUploadDir;

    private Path profilesDirectory;

    @PostConstruct
    public void initialize() throws IOException {
        profilesDirectory = Paths.get(profilesUploadDir).toAbsolutePath().normalize();
        Files.createDirectories(profilesDirectory);
    }

    public boolean isAllowedImage(String filename) {
        if (filename == null) {
            return false;
        }
        String name = Paths.get(filename).getFileName().toString();
        int extensionSeparator = name.lastIndexOf('.');
        if (extensionSeparator < 0 || extensionSeparator == name.length() - 1) {
            return false;
        }
        return ALLOWED_IMAGE_EXTENSIONS.contains(name.substring(extensionSeparator + 1).toLowerCase(Locale.ROOT));
    }

    public String storeProfilePhoto(MultipartFile file, String userId) throws IOException {
        String originalName = file.getOriginalFilename();
        if (file.isEmpty() || !isAllowedImage(originalName)) {
            throw new IllegalArgumentException("Formato de imagen no permitido o archivo vacío.");
        }

        String filename = Paths.get(originalName).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
        String storedFilename = userId + "_" + filename;
        Path destination = profilesDirectory.resolve(storedFilename).normalize();
        if (!destination.getParent().equals(profilesDirectory)) {
            throw new IllegalArgumentException("Nombre de archivo no válido.");
        }
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return storedFilename;
    }
}
