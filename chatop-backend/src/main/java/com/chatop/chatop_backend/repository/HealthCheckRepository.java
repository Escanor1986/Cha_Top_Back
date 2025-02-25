package com.chatop.chatop_backend.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

@Repository
public class HealthCheckRepository {
  
  // ✅ Injection via le constructeur (recommandé) plutôt que par @Autowired
  // EntityManager est une interface standard JPA, elle est donc injectée par Spring
  // EntityManager fournit des méthodes pour interagir avec la base de données
  // EntityManager est utilisé pour exécuter des requêtes SQL et des opérations CRUD
  private final EntityManager entityManager;

    HealthCheckRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

  public Long countApplicationConnections() {
    String applicationConnectionsQuery = "SELECT COUNT(*) FROM pg_stat_activity WHERE application_name = 'PostgreSQL JDBC Driver'";
    // Exécute la requête SQL pour compter le nombre de connexions d'application actives
    // Renvoie le résultat sous forme de Long
    // createNativeQuery est utilisé pour exécuter des requêtes SQL natives
    // getSingleResult est utilisé pour obtenir un seul résultat (ici, le nombre de connexions d'application)
    return (Long) entityManager.createNativeQuery(applicationConnectionsQuery).getSingleResult();
  }
}
