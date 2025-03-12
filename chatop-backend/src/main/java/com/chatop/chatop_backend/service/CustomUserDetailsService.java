package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service pour charger les détails de l'utilisateur à partir de la base de données.
 * Cette classe est utilisée par Spring Security pour l'authentification.
 * Implémente l'interface UserDetailsService de Spring Security.
 * @see org.springframework.security.core.userdetails.UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Charge les détails de l'utilisateur à partir de son email.
     *
     * @param email Email de l'utilisateur
     * @return Détails de l'utilisateur
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
