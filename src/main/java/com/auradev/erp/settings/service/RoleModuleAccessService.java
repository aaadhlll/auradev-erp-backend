package com.auradev.erp.settings.service;

import com.auradev.erp.auth.security.AppModule;
import com.auradev.erp.auth.security.Permission;
import com.auradev.erp.auth.security.RolePermissions;
import com.auradev.erp.settings.dto.RoleModuleAccessResponse;
import com.auradev.erp.settings.dto.UpdateRoleModuleAccessRequest;
import com.auradev.erp.settings.entity.RoleModuleAccess;
import com.auradev.erp.settings.repository.RoleModuleAccessRepository;
import com.auradev.erp.tenant.TenantContext;
import com.auradev.erp.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleModuleAccessService {

    private final RoleModuleAccessRepository repository;

    @Transactional(readOnly = true)
    public boolean isEnabled(UUID tenantId, UserRole role, AppModule module) {
        if (role == UserRole.SUPER_ADMIN) return true;
        if (!supports(role, module)) return false;
        if (role == UserRole.TENANT_ADMIN && module == AppModule.ADMINISTRATION) return true;
        List<RoleModuleAccess> configured = repository.findAllByTenantIdAndRole(tenantId, role);
        if (configured.isEmpty()) return defaultModules(role).contains(module);
        return configured.stream().anyMatch(row -> row.getModule() == module && row.isEnabled());
    }

    @Transactional(readOnly = true)
    public List<String> enabledForCurrentTenant(UserRole role) {
        UUID tenantId = TenantContext.require();
        return Arrays.stream(AppModule.values())
                .filter(module -> isEnabled(tenantId, role, module))
                .map(AppModule::name)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleModuleAccessResponse getMatrix() {
        Map<String, List<String>> matrix = Arrays.stream(UserRole.values())
                .filter(role -> role != UserRole.SUPER_ADMIN)
                .collect(Collectors.toMap(UserRole::name, this::enabledForCurrentTenant, (a, b) -> a, LinkedHashMap::new));
        return new RoleModuleAccessResponse(matrix);
    }

    @Transactional
    public RoleModuleAccessResponse updateMatrix(UpdateRoleModuleAccessRequest request) {
        UUID tenantId = TenantContext.require();
        for (Map.Entry<String, List<String>> entry : request.roles().entrySet()) {
            UserRole role = UserRole.valueOf(entry.getKey());
            if (role == UserRole.SUPER_ADMIN) continue;
            Set<AppModule> enabled = entry.getValue().stream().map(AppModule::valueOf).collect(Collectors.toSet());
            if (role == UserRole.TENANT_ADMIN) enabled.add(AppModule.ADMINISTRATION);
            Map<AppModule, RoleModuleAccess> existing = repository.findAllByTenantIdAndRole(tenantId, role).stream()
                    .collect(Collectors.toMap(RoleModuleAccess::getModule, row -> row));
            for (AppModule module : AppModule.values()) {
                RoleModuleAccess row = existing.getOrDefault(module, new RoleModuleAccess());
                if (row.getId() == null) {
                    row.setTenantId(tenantId);
                    row.setRole(role);
                    row.setModule(module);
                }
                row.setEnabled(enabled.contains(module));
                repository.save(row);
            }
        }
        return getMatrix();
    }

    private static Set<AppModule> defaultModules(UserRole role) {
        return switch (role) {
            case SUPER_ADMIN, TENANT_ADMIN, MANAGER -> EnumSet.allOf(AppModule.class);
            case CASHIER -> EnumSet.of(AppModule.POS);
            case INVENTORY_STAFF -> EnumSet.of(AppModule.INVENTORY, AppModule.PROCUREMENT);
            case PROCUREMENT_STAFF -> EnumSet.of(AppModule.PROCUREMENT);
            case ACCOUNTANT -> EnumSet.of(AppModule.OVERVIEW);
        };
    }

    private static boolean supports(UserRole role, AppModule module) {
        return switch (module) {
            case OVERVIEW -> RolePermissions.has(role, Permission.DASHBOARD_VIEW);
            case POS -> RolePermissions.has(role, Permission.BILL_CREATE) || RolePermissions.has(role, Permission.BILL_VIEW);
            case INVENTORY -> RolePermissions.has(role, Permission.INVENTORY_VIEW);
            case PROCUREMENT -> RolePermissions.has(role, Permission.PURCHASE_VIEW);
            case ADMINISTRATION -> RolePermissions.has(role, Permission.SETTINGS_STORE_VIEW)
                    || RolePermissions.has(role, Permission.SETTINGS_USERS_MANAGE)
                    || RolePermissions.has(role, Permission.AUDIT_VIEW);
        };
    }
}
