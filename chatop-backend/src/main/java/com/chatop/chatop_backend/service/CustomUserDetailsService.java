package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service pour charger les détails de l'utilisateur à partir de la base de données.
 * Utilisé par Spring Security pour l'authentification et l'autorisation.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // S'assurer que le rôle commence bien par "ROLE_"
        String role = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
