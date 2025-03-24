package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * Service pour charger les détails de l'utilisateur à partir de la base de données.
 * Utilisé par Spring Security pour l'authentification et l'autorisation.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    /**
     * Charge les détails de l'utilisateur à partir de son email.
     * - Vérifie si l'utilisateur existe en base de données.
     * - Associe son rôle avec `ROLE_` pour respecter les conventions Spring Security.
     *
     * @param email Email de l'utilisateur
     * @return Détails de l'utilisateur
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("🔍 Recherche de l'utilisateur par email: {}", email);
        
        try {
            // Recherche l'utilisateur et log le résultat
            Optional<User> userOptional = userRepository.findByEmail(email);
            log.debug("👤 Existence de l'utilisateur dans la base: {}", userOptional.isPresent());
            
            if (userOptional.isEmpty()) {
                log.error("❌ Utilisateur non trouvé avec l'email: {}", email);
                throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
            }
            
            User user = userOptional.get();
            
            // Journalisation des informations utilisateur (sans données sensibles)
            log.debug("✅ Utilisateur trouvé: id={}, email={}, rôle={}, création={}", 
                    user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());

            // S'assurer que le rôle commence bien par "ROLE_"
            String role = user.getRole();
            if (role == null || role.isEmpty()) {
                log.warn("⚠️ L'utilisateur {} n'a pas de rôle défini, attribution du rôle par défaut ROLE_USER", user.getId());
                role = "ROLE_USER";
            } else if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            
            log.debug("🔒 Rôle utilisé pour l'authentification: {}", role);

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
        } catch (UsernameNotFoundException e) {
            // Propagation de l'exception spécifique
            throw e;
        } catch (Exception e) {
            // En cas d'erreur inattendue, loguer l'erreur et lancer une exception UsernameNotFoundException
            log.error("🔥 Erreur inattendue lors du chargement de l'utilisateur: {}", e.getMessage(), e);
            throw new UsernameNotFoundException("Erreur lors du chargement de l'utilisateur: " + e.getMessage());
        }
    }
}
