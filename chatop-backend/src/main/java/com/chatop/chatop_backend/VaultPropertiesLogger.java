package com.chatop.chatop_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class VaultPropertiesLogger {

  private static final Logger logger = LoggerFactory.getLogger(VaultPropertiesLogger.class);


    @Value("${jwt.secret:NOT_FOUND}")
    private String jwtSecret;

    @PostConstruct
    public void logVaultProperties() {
        logger.info("🔍 Valeur de jwt.secret récupérée depuis Vault : {}", jwtSecret);
    }
}
