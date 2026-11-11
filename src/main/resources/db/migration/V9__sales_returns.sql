CREATE TABLE sales_returns (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    bill_id         UUID NOT NULL REFERENCES bills(id),
    return_no       VARCHAR(40) NOT NULL,
    reason          TEXT NOT NULL,
    refund_method   payment_method NOT NULL,
    refund_total    NUMERIC(12,2) NOT NULL,
    created_by      UUID NOT NULL REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, return_no)
);
CREATE INDEX idx_sales_returns_bill ON sales_returns(tenant_id, bill_id, created_at DESC);

CREATE TABLE sales_return_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_id       UUID NOT NULL REFERENCES sales_returns(id) ON DELETE CASCADE,
    bill_item_id    UUID NOT NULL REFERENCES bill_items(id),
    product_id      UUID NOT NULL REFERENCES products(id),
    quantity        NUMERIC(12,3) NOT NULL CHECK (quantity > 0),
    refund_amount   NUMERIC(12,2) NOT NULL CHECK (refund_amount >= 0)
);
CREATE INDEX idx_sales_return_items_bill_item ON sales_return_items(bill_item_id);
