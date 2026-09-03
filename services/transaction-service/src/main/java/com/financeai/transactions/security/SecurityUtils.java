package com.financeai.transactions.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getCredentials() instanceof Long userId) {
                return userId;
            }
            if (auth.getPrincipal() instanceof Long userId) {
                return userId;
            }
        }
        return null;
    }

    public static void validateUserOwnership(Long targetUserId) {
        Long authenticatedId = getAuthenticatedUserId();
        // Si no se puede determinar el ID autenticado (ej. petición no autenticada o token inválido)
        if (authenticatedId == null || !authenticatedId.equals(targetUserId)) {
            throw new AccessDeniedException("Acceso denegado: no tiene autorización para acceder o modificar los recursos de este usuario.");
        }
    }
}
