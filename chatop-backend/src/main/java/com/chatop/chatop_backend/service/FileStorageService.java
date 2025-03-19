package com.chatop.chatop_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Cette classe est un service qui permet de gérer le stockage des fichiers.
 * Elle permet de sauvegarder un fichier sur le serveur.
 * 
 * @Service: Indique à Spring qu'il s'agit d'un service.
 */
@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "./uploads/";
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    public String saveFile(MultipartFile file) throws IOException {
        log.debug("📁 Tentative de sauvegarde du fichier: {}, taille: {} octets", 
                file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            log.warn("⚠️ Fichier vide soumis");
            return null;
        }

        // Vérification du répertoire de téléchargement
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            log.debug("📂 Création du répertoire de téléchargement: {}", uploadDir.getAbsolutePath());
            boolean created = uploadDir.mkdirs();
            log.debug("📂 Répertoire créé: {}", created);
        }

        // Génération d'un nom de fichier unique pour éviter les collisions
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        String filePath = UPLOAD_DIR + uniqueFilename;
        log.debug("💾 Sauvegarde du fichier vers: {}", filePath);
        
        try {
            Path path = Paths.get(filePath);
            Files.copy(file.getInputStream(), path);
            log.debug("✅ Fichier sauvegardé avec succès");
            
            // Vérification des permissions du fichier
            log.debug("📄 Vérification des permissions - Lecture: {}, Écriture: {}", 
                    Files.isReadable(path), Files.isWritable(path));
            
            return "/uploads/" + uniqueFilename; // Chemin accessible via l'API
        } catch (IOException e) {
            log.error("❌ Échec de la sauvegarde du fichier: {}", e.getMessage(), e);
            throw e;
        }
    }
}
