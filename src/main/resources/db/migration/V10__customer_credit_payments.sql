CREATE TABLE customer_credit_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    customer_id UUID NOT NULL REFERENCES customers(id),
    payment_no VARCHAR(40) NOT NULL,
    amount NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    balance_after NUMERIC(12,2) NOT NULL,
    payment_method payment_method NOT NULL,
    notes TEXT,
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_customer_credit_payments_tenant_no UNIQUE (tenant_id, payment_no)
);

CREATE INDEX idx_customer_credit_payments_customer_created
    ON customer_credit_payments (tenant_id, customer_id, created_at DESC);
