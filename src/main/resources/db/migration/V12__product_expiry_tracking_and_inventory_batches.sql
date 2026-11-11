ALTER TABLE products
    ADD COLUMN IF NOT EXISTS expiry_tracked BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS inventory_batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    product_id UUID NOT NULL REFERENCES products(id),
    batch_number VARCHAR(100) NOT NULL,
    expiry_date DATE,
    received_date DATE NOT NULL DEFAULT CURRENT_DATE,
    quantity_received NUMERIC(12,3) NOT NULL CHECK (quantity_received > 0),
    quantity_on_hand NUMERIC(12,3) NOT NULL CHECK (quantity_on_hand >= 0),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID NOT NULL REFERENCES users(id),
    UNIQUE (tenant_id, product_id, batch_number)
);

CREATE INDEX IF NOT EXISTS idx_inventory_batches_tenant_product_expiry
    ON inventory_batches (tenant_id, product_id, expiry_date);
