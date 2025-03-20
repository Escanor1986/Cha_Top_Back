package com.chatop.chatop_backend.service;

import org.springframework.stereotype.Service;

import com.chatop.chatop_backend.ApplicationStatus;
import com.chatop.chatop_backend.HealthCheck;
import com.chatop.chatop_backend.repository.HealthCheckRepository;

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
