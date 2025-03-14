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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
        // Récupère l'en-tête Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;


        // Vérifie si l'en-tête Authorization est présent et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si non, passe au filtre suivant
            log.info("[DEBUG] Pas de header Bearer -> on laisse passer la requête sans auth");
            filterChain.doFilter(request, response);
            return;
        }
        
        //! Log de debogage pour l'en-tête Authorization
        log.info("Authorization: {}", authHeader);

        // Extrait le token JWT (en supprimant le préfixe "Bearer ")
        jwt = authHeader.substring(7);
        
        // Extrait l'email de l'utilisateur depuis le token
        userEmail = jwtService.extractUsername(jwt);

        log.info("[DEBUG] JWT = {}", jwt);
        log.info("[DEBUG] userEmail = {}", userEmail);

        
        // Vérifie si l'email existe et si l'utilisateur n'est pas déjà authentifié
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Charge les détails de l'utilisateur depuis la base de données
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // Vérifie si le token est valide pour cet utilisateur
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Crée un token d'authentification Spring Security
                log.info("[DEBUG] Token VALIDE, on authentifie {}", userDetails.getUsername());
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
            } else {
                // Si le token n'est pas valide, envoie une erreur 403
                log.info("[DEBUG] Token INVALIDE, on envoie un 403 / on laisse le filterChain gérer");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
                return;
            }
        }
        
        // Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
