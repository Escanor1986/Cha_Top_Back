package com.chatop.chatop_backend.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.chatop_backend.HealthCheck;
import com.chatop.chatop_backend.service.HealthCheckService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ? Cette classe est un contrôleur REST qui expose un point de terminaison pour
 * vérifier l'état de l'application.
 * ? Le point de terminaison /healthcheck renvoie un objet HealthCheck qui
 * contient le statut de l'application et un message.
 * ? Le contrôleur utilise le service HealthCheckService pour obtenir le statut
 * de l'application.
 * ? Le service HealthCheckService renvoie un objet HealthCheck avec le statut
 * OK et un message de bienvenue.
 * ? Le contrôleur REST renvoie l'objet HealthCheck en réponse à la requête GET
 * sur le point de terminaison /healthcheck.
 */
@Tag(name = "HealthCheck API Controller", description = "Exposes an endpoint to check the application status")
@RestController
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    // ✅ Injection via le constructeur (recommandé) plutôt que par @Autowired
    // Plus facile à tester et plus sûr car les dépendances sont explicites
    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @Operation(summary = "Check the application status", description = "Returns the application status and a welcome message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is running", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = HealthCheck.class)) })
    })

    @GetMapping("/healthcheck")
    // Utilisation du service HealthCheckService pour obtenir le statut de
    // l'application
    public HealthCheck healthcheck() {
        return healthCheckService.healthcheck();
    }
}
