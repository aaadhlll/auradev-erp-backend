ALTER TYPE user_role ADD VALUE IF NOT EXISTS 'PROCUREMENT_STAFF';

CREATE TABLE role_module_access (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    role        user_role NOT NULL,
    module      VARCHAR(32) NOT NULL,
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    version     BIGINT NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  UUID REFERENCES users(id),
    updated_by  UUID REFERENCES users(id),
    CONSTRAINT uq_role_module_access UNIQUE (tenant_id, role, module),
    CONSTRAINT ck_role_module CHECK (module IN ('OVERVIEW', 'POS', 'INVENTORY', 'PROCUREMENT', 'ADMINISTRATION'))
);

CREATE INDEX idx_role_module_access_tenant_role ON role_module_access(tenant_id, role);

-- Safe defaults for existing tenants. Tenant Admin cannot be locked out of Administration.
INSERT INTO role_module_access (tenant_id, role, module, enabled)
SELECT t.id, r.role::user_role, m.module, TRUE
FROM tenants t
CROSS JOIN (VALUES
    ('TENANT_ADMIN'), ('MANAGER')
) AS r(role)
CROSS JOIN (VALUES
    ('OVERVIEW'), ('POS'), ('INVENTORY'), ('PROCUREMENT'), ('ADMINISTRATION')
) AS m(module);

INSERT INTO role_module_access (tenant_id, role, module, enabled)
SELECT t.id, 'CASHIER'::user_role, 'POS', TRUE FROM tenants t;

INSERT INTO role_module_access (tenant_id, role, module, enabled)
SELECT t.id, 'INVENTORY_STAFF'::user_role, m.module, TRUE
FROM tenants t CROSS JOIN (VALUES ('INVENTORY'), ('PROCUREMENT')) AS m(module);

INSERT INTO role_module_access (tenant_id, role, module, enabled)
SELECT t.id, 'ACCOUNTANT'::user_role, 'OVERVIEW', TRUE FROM tenants t;
