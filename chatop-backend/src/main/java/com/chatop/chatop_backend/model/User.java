package com.chatop.chatop_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Représente un utilisateur de l'application, stocké dans la base de données.
 * Cette classe implémente {@link UserDetails} pour être compatible avec Spring Security.
 */
@Entity
@Table(name = "USERS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * Identifiant unique de l'utilisateur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email de l'utilisateur (doit être unique).
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Nom complet de l'utilisateur.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Mot de passe de l'utilisateur (sera stocké sous forme hashée).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Date et heure de création du compte.
     * Cette valeur ne doit pas être modifiée après l'insertion.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière mise à jour du compte.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Liste des locations (Rentals) appartenant à cet utilisateur.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentals;

    /**
     * Récupère les rôles et autorisations de l'utilisateur.
     *
     * @return Liste des autorités accordées à l'utilisateur.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Aucun rôle pour le moment
    }

    /**
     * Récupère le nom d'utilisateur utilisé par Spring Security pour l'authentification.
     *
     * @return L'email de l'utilisateur.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indique si le compte de l'utilisateur a expiré.
     *
     * @return `true` si le compte est valide.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indique si le compte est verrouillé.
     *
     * @return `true` si le compte est actif.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indique si les informations d'authentification de l'utilisateur ont expiré.
     *
     * @return `true` si les informations sont valides.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indique si l'utilisateur est activé et peut s'authentifier.
     *
     * @return `true` si l'utilisateur est actif.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
