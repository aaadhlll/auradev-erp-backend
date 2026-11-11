package com.auradev.erp.settings.repository;

import com.auradev.erp.settings.entity.RoleModuleAccess;
import com.auradev.erp.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoleModuleAccessRepository extends JpaRepository<RoleModuleAccess, UUID> {
    List<RoleModuleAccess> findAllByTenantId(UUID tenantId);
    List<RoleModuleAccess> findAllByTenantIdAndRole(UUID tenantId, UserRole role);
}
