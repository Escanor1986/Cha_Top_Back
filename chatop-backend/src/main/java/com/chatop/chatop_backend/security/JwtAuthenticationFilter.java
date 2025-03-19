package com.chatop.chatop_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

/**
 * Filtre pour intercepter les requêtes HTTP et vérifier l'authentification JWT.
 * Ce filtre vérifie si un token JWT valide est présent dans l'en-tête Authorization.
 * Il extrait l'email de l'utilisateur depuis le token et charge les détails de l'utilisateur depuis la base de données.
 * Si l'utilisateur est authentifié, il crée un token d'authentification Spring Security et met à jour le contexte de sécurité.
 * @Component indique que cette classe est un composant Spring.
 * @RequiredArgsConstructor génère un constructeur avec tous les champs en lecture seule.
 * @see com.chatop.chatop_backend.security.JwtService
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Logs sur la requête entrante
        log.debug("🔍 Traitement de la requête: {} {}", request.getMethod(), request.getRequestURI());
        
        // Récupère l'en-tête Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Vérifie si l'en-tête Authorization est présent et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si non, passe au filtre suivant
            log.debug("⚠️ Pas de header Bearer -> on laisse passer la requête sans auth");
            filterChain.doFilter(request, response);
            return;
        }
        
        // Log de débogage pour l'en-tête Authorization (version masquée pour la sécurité)
        String maskedAuth = authHeader.substring(0, 15) + "..." + authHeader.substring(authHeader.length() - 10);
        log.debug("🔐 En-tête d'autorisation détecté: {}", maskedAuth);

        // Extrait le token JWT (en supprimant le préfixe "Bearer ")
        jwt = authHeader.substring(7);
        
        try {
            // Extrait l'email de l'utilisateur depuis le token
            userEmail = jwtService.extractUsername(jwt);
            log.debug("👤 Email extrait du token: {}", userEmail);
            
            // Vérifie et log la date d'expiration du token
            Date expiration = jwtService.extractExpiration(jwt);
            log.debug("⏱️ Expiration du token: {}, Token valide encore: {} secondes", 
                     expiration, 
                     (expiration.getTime() - System.currentTimeMillis()) / 1000);
            
            // Vérifie si l'email existe et si l'utilisateur n'est pas déjà authentifié
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("🔍 Tentative de chargement de l'utilisateur avec l'email: {}", userEmail);
                
                try {
                    // Charge les détails de l'utilisateur depuis la base de données
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    log.debug("✅ Utilisateur chargé avec succès: {}", userDetails.getUsername());
                    
                    // Vérifie si le token est valide pour cet utilisateur
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // Crée un token d'authentification Spring Security
                        log.debug("✅ Token VALIDE, authentification de l'utilisateur: {}", userDetails.getUsername());
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        
                        // Ajoute les détails de la requête
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        
                        // Met à jour le contexte de sécurité avec l'authentification
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("🔒 Contexte de sécurité mis à jour avec l'authentification");
                    } else {
                        // Si le token n'est pas valide, envoie une erreur 403
                        log.warn("❌ Token INVALIDE pour l'utilisateur: {}", userEmail);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token invalide");
                        return;
                    }
                } catch (UsernameNotFoundException e) {
                    log.error("❌ Échec de chargement de l'utilisateur: {}", userEmail, e);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non trouvé");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors du traitement du token JWT: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Erreur de token: " + e.getMessage());
            return;
        }
        
        // Continue la chaîne de filtres
        log.debug("⏩ Poursuite de la chaîne de filtres");
        filterChain.doFilter(request, response);
    }
}
