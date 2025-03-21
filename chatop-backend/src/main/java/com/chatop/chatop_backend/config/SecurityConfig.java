package com.chatop.chatop_backend.config;

import com.chatop.chatop_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration de Spring Security pour l'application.
 * Cette classe configure la sécurité HTTP et les filtres d'authentification.
 * Elle définit les chemins publics et privés, les fournisseurs
 * d'authentification et les encodeurs de mot de passe.
 * Elle configure également CORS pour permettre les requêtes cross-origin.
 * 
 * @Configuration indique que cette classe contient des méthodes de
 *                configuration.
 * @EnableWebSecurity active la sécurité Web pour l'application.
 * @RequiredArgsConstructor génère un constructeur avec tous les champs en
 *                          lecture seule.
 * @see com.chatop.chatop_backend.security.JwtAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Configure la chaîne de filtres de sécurité.
     *
     * @param http Configuration de la sécurité HTTP
     * @return Chaîne de filtres de sécurité configurée
     * @Bean indique que cette méthode produit un bean à utiliser dans l'application. un bean est un objet qui est instancié, assemblé et géré par un conteneur IoC (Inversion of Control). Il sert de composant dans le développement d'applications. un conteneur IoC est un cadre logiciel qui gère les beans. Le Bean est utile quand vous avez besoin d'un objet à plusieurs endroits dans votre application.
     * @throws Exception si une erreur se produit lors de la configuration de la sécurité
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http        
                .csrf(AbstractHttpConfigurer::disable) // Désactive la protection CSRF. CSRF signifie Cross-Site Request Forgery. Il s'agit d'une attaque qui force un utilisateur à exécuter des actions non désirées sur une application Web dans laquelle il est authentifié.
                            // Configuration des en-têtes de sécurité HTTP
                .headers(headers -> headers
                    // Content Security Policy : Définit les sources autorisées pour le contenu
                    .contentSecurityPolicy(csp -> csp
                        .policyDirectives("default-src 'self';  style-src 'self' 'unsafe-inline';"))
                    // X-Frame-Options : Empêche le site d'être affiché dans un iframe (protection contre le clickjacking)
                    .frameOptions(frameOptions -> frameOptions.deny())
                    // Cache-Control : Désactive la mise en cache pour les ressources sensibles pour éviter les fuites d'informations
                    .cacheControl(cacheControl -> cacheControl.disable())
                    // HSTS : Active la politique de sécurité de transport strict (HSTS) pour forcer HTTPS et protéger contre les attaques de type SSLStrip
                    .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configure CORS pour permettre les requêtes cross-origin. CORS signifie Cross-Origin Resource Sharing. Il s'agit d'un mécanisme qui utilise des en-têtes HTTP pour permettre à un serveur de dire à un navigateur web d'accéder à des ressources d'un serveur situé sur un autre domaine.
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/uploads/**").permitAll() 
                        .requestMatchers(
                                "/", // Chemin racine pour rediriger vers Swagger UI
                                "/healthcheck",
                                "/api/auth/**",
                                "/api-docs/**",
                                "/api-docs/swagger-config",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**").permitAll()
                                .requestMatchers("/api/rentals/**").authenticated() 
                                .requestMatchers("/api/messages/**").authenticated()
                        // Chemin pour les locations qui nécessitent une authentification
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Désactive la gestion de session pour que Spring Security ne crée pas de session HTTP
                .authenticationProvider(authenticationProvider()) // Configure le fournisseur d'authentification qui vérifie les identifiants de l'utilisateur
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Ajoute le filtre d'authentification JWT avant le filtre d'authentification par nom d'utilisateur et mot de passe

        return http.build(); // Construit la chaîne de filtres de sécurité configurée et la retourne pour être utilisée dans l'application.
    }

    /**
     * Configure CORS pour permettre les requêtes cross-origin.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200, http://localhost:8888"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition")); // Expose l'en-tête Content-Disposition pour le téléchargement de fichiers (ref aux images dans uploads)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Fournit un encodeur de mot de passe pour hacher les mots de passe avec un bean pour n'avoir qu'une seule instance dans l'application.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure le fournisseur d'authentification qui vérifie les identifiants de
     * l'utilisateur.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configure le gestionnaire d'authentification.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
