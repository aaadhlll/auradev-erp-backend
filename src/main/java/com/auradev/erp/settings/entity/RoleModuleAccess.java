package com.auradev.erp.settings.entity;

import com.auradev.erp.auth.security.AppModule;
import com.auradev.erp.common.entity.BaseEntity;
import com.auradev.erp.user.entity.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "role_module_access", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "role", "module"}))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class RoleModuleAccess extends BaseEntity {

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "module", nullable = false, length = 32)
    private AppModule module;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}
