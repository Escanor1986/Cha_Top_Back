package com.chatop.chatop_backend.service;

import org.springframework.stereotype.Service;

import com.chatop.chatop_backend.ApplicationStatus;
import com.chatop.chatop_backend.HealthCheck;
import com.chatop.chatop_backend.repository.HealthCheckRepository;

/**
 * Service class pour la vérification de l'état de l'application.
 * Cette classe vérifie si l'application est en cours d'exécution correctement.
 * Elle utilise le HealthCheckRepository pour compter les connexions actives à la base de données.
 * 
 * @Service indique que cette classe est un bean de service.
 */
@Service
public class HealthCheckService {

  private final HealthCheckRepository healthCheckRepository;

  public HealthCheckService(HealthCheckRepository healthCheckRepository) {
    this.healthCheckRepository = healthCheckRepository;
  }

  public HealthCheck healthcheck() {
    Long activeSessions = healthCheckRepository.countApplicationConnections();

    if (activeSessions > 0) {
      return new HealthCheck(ApplicationStatus.OK, "Welcome to escanor1986 Tennis! Active PostgreSQL Sessions: " + activeSessions);
    } else {
      return new HealthCheck(ApplicationStatus.KO, "Database Connection failed!");
    }
  }
}
