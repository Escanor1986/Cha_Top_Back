package com.chatop.chatop_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Permet d'accéder aux fichiers du dossier "uploads" via "/uploads/"
        registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:./uploads/") // Ajoute "./" pour éviter les erreurs de chemin relatif
        .setCachePeriod(3600); // Cache pour 1h (optionnel)
    }
}
