package com.chatop.chatop_backend.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de sécurité globale pour l'application.
 * 
 * Cette classe définit les règles de sécurité, de gestion des autorisations
 * et configure le filtre JWT personnalisé pour l'authentification.
 * 
 * @author Votre Nom
 * @version 1.0
 * @since 2024-03-04
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Constructeur de la configuration de sécurité.
     * 
     * @param jwtAuthFilter Filtre personnalisé pour l'authentification JWT
     */
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Définit la chaîne de filtres de sécurité pour l'application.
     * 
     * Configure les règles de sécurité HTTP, notamment :
     * - Désactivation de la protection CSRF
     * - Définition des routes publiques et authentifiées
     * - Ajout du filtre JWT personnalisé
     * 
     * @param http Configuration de sécurité HTTP
     * @return La chaîne de filtres de sécurité configurée
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // Routes publiques
                .anyRequest().authenticated()  // Autres routes nécessitent authentification
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
