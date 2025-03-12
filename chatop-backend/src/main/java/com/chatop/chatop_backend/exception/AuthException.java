package com.chatop.chatop_backend.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception personnalisée pour gérer les erreurs d'authentification et d'autorisation.
 */
public class AuthException extends AuthenticationException {
    
    private static final long serialVersionUID = 1L;
    
    private String errorCode;

    /**
     * Constructeur avec message d'erreur.
     * 
     * @param message Le message décrivant l'erreur
     */
    public AuthException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec message d'erreur et code d'erreur.
     * 
     * @param message Le message décrivant l'erreur
     * @param errorCode Code d'erreur spécifique pour identification précise du type d'erreur
     */
    public AuthException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Retourne le code d'erreur associé à cette exception.
     * 
     * @return Le code d'erreur
     */
    public String getErrorCode() {
        return errorCode;
    }
}
