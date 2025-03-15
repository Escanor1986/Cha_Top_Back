package com.chatop.chatop_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Cette classe est un service qui permet de gérer le stockage des fichiers.
 * Elle permet de sauvegarder un fichier sur le serveur.
 * 
 * @Service: Indique à Spring qu'il s'agit d'un service.
  */
@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "/app/uploads/";

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String filePath = UPLOAD_DIR + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return "/uploads/" + file.getOriginalFilename(); // Chemin accessible via l'API
    }
}
