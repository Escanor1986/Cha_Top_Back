package com.chatop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de l'API OpenAPI (Swagger)
 * Permet de définir les informations de l'API et les paramètres de sécurité
 * (JWT Bearer Token)
 * 
 * @Configuration : Indique à Spring que cette classe est une classe de
 *                configuration
 * @Bean : Indique à Spring de gérer l'instanciation de l'objet retourné par la
 *       méthode customOpenAPI()
 * @return : Retourne un objet OpenAPI
 */
@Configuration
public class OpenApiConfig {

  /**
   * Configuration de l'API OpenAPI (Swagger)
   * 
   * @return : Retourne un objet OpenAPI
   *         Cette méthode permet de définir les informations de l'API et les
   *         paramètres de sécurité.
   *         Avec Swagger, on peut définir les informations de l'API (titre,
   *         description, version) et les paramètres de sécurité.
   *         Ici, on définit un paramètre de sécurité de type Bearer Token (JWT)
   *         pour sécuriser l'accès à l'API.
   *         On définit également un composant de sécurité (SecurityScheme) qui
   *         permet de définir le type de sécurité (HTTP),
   *         le schéma de sécurité (Bearer) et le format du token (JWT).
   *         Enfin, on ajoute un élément de sécurité (SecurityRequirement) qui
   *         permet de définir le nom du schéma de sécurité.
   */
  @Bean
  public OpenAPI customOpenAPI() {

    final String securitySchemeName = "bearerAuth";

    return new OpenAPI()
        .info(new Info()
            .title("ChatOp API")
            .description("API pour la gestion des locations et messages")
            .version("1.0.0"))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(new Components()
            .addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/api/**")
        .build();
  }
}
