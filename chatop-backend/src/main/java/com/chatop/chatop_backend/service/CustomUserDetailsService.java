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
     * 
     * !loadUserByUsername doit retourner un objet UserDetails qui contient les informations de l'utilisateur
     * !et ses rôles/permissions. Même si votre application n'utilise pas de rôles complexes,
     * !vous devez au minimum fournir une liste d'autorités (qui peut être vide ou contenir un rôle par défaut comme "USER").
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("🔍 Recherche de l'utilisateur par email: {}", email);
        
        // Recherche l'utilisateur et log le résultat
        Optional<User> userOptional = userRepository.findByEmail(email);
        log.debug("👤 Existence de l'utilisateur dans la base: {}", userOptional.isPresent());
        
        if (userOptional.isEmpty()) {
            log.error("❌ Utilisateur non trouvé avec l'email: {}", email);
            throw new UsernameNotFoundException("Utilisateur non trouvé");
        }
        
        User user = userOptional.get();
        
        // Journalisation des informations utilisateur (sans données sensibles)
        log.debug("✅ Utilisateur trouvé: id={}, email={}, rôle={}, création={}", 
                user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());

        // S'assurer que le rôle commence bien par "ROLE_"
        String role = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();
        log.debug("🔒 Rôle utilisé pour l'authentification: {}", role);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}

/* 
 * Remarque:
 * UserDetailsService dans Spring Security exige obligatoirement la gestion des rôles (ou "authorities") pour plusieurs raisons:

1. La sécurité dans Spring est basée sur l'authentification (qui est l'utilisateur) ET l'autorisation (ce qu'il peut faire)

2. La méthode `loadUserByUsername()` doit retourner un `UserDetails` qui contient nécessairement:
   - Identifiants de connexion
   - Mot de passe
   - Collection d'`GrantedAuthority` représentant les rôles/permissions

3. Ces autorités sont utilisées pour les annotations comme `@PreAuthorize("hasRole('ADMIN')")` et les configurations de sécurité comme `.hasAuthority("WRITE_PRIVILEGE")`

Même si votre application n'utilise pas de rôles complexes, vous devez au minimum fournir une liste d'autorités (qui peut être vide ou contenir un rôle par défaut comme "USER").

Dans l'implémentation:
```java
return new User(username, password, 
    Ici, vous DEVEZ spécifier une collection d'autorités
    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
```
 */
