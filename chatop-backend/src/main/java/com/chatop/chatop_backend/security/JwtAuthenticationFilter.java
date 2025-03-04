package com.chatop.chatop_backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification personnalisé pour la validation des tokens JWT.
 * 
 * Ce filtre intercepte chaque requête HTTP et vérifie la présence d'un token JWT valide 
 * dans l'en-tête Authorization. Il extrait et valide l'utilisateur avant de configurer 
 * le contexte de sécurité de Spring.
 * 
 * @author Votre Nom
 * @version 1.0
 * @since 2024-03-04
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructeur du filtre JWT.
     * 
     * @param jwtService Service de gestion des tokens JWT
     * @param userDetailsService Service de gestion des détails utilisateur
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Méthode principale de filtrage des requêtes HTTP.
     * 
     * Extrait le token JWT de l'en-tête Authorization, valide son authenticité,
     * charge les détails de l'utilisateur et configure le contexte de sécurité.
     * 
     * @param request Requête HTTP entrante
     * @param response Réponse HTTP
     * @param filterChain Chaîne de filtres à exécuter
     * @throws ServletException En cas d'erreur de servlet
     * @throws IOException En cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Vérification de la présence et du format du token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token JWT
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractClaim(jwt, Claims::getSubject);

        // Vérification si l'utilisateur n'est pas déjà authentifié
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Validation du token
            if (jwtService.validateToken(jwt, userEmail)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
                );

                // Définition du contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
