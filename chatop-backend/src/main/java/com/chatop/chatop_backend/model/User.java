package com.chatop.chatop_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
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
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private transient List<Rental> rentals;

    /**
     * Ajout du rôle de l'utilisateur pour la gestion des permissions.
     */
    @Column(nullable = false)
    @Builder.Default
    private String role = "ROLE_USER"; 

    /**
     * Récupère le rôle de l'utilisateur.
     *
     * @return Le rôle sous forme de chaîne de caractères.
     */
    public String getRole() {
        return role;
    }

    /**
     * Récupère les rôles et autorisations de l'utilisateur.
     *
     * @return Liste des autorités accordées à l'utilisateur.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
