package com.auradev.erp.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.auradev.erp.settings.service.RoleModuleAccessService;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Component("authz")
@RequiredArgsConstructor
public class PermissionChecker {

    private final RoleModuleAccessService moduleAccessService;

    public boolean can(Authentication authentication, String permission) {
        return resolve(authentication)
                .map(p -> allowed(p, Permission.valueOf(permission)))
                .orElse(false);
    }

    public boolean canAny(Authentication authentication, String... permissions) {
        return resolve(authentication)
                .map(p -> Arrays.stream(permissions)
                        .anyMatch(perm -> allowed(p, Permission.valueOf(perm))))
                .orElse(false);
    }

    private boolean allowed(UserPrincipal principal, Permission permission) {
        if (!RolePermissions.has(principal.getRole(), permission)) return false;
        if (principal.getTenantId() == null) return true;
        return moduleAccessService.isEnabled(
                principal.getTenantId(), principal.getRole(), AppModule.forPermission(permission));
    }

    private static Optional<UserPrincipal> resolve(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }
}
