ALTER TABLE tenants
    ADD COLUMN IF NOT EXISTS currency VARCHAR(3) NOT NULL DEFAULT 'QAR';

ALTER TABLE tenants
    DROP CONSTRAINT IF EXISTS tenants_currency_check;

ALTER TABLE tenants
    ADD CONSTRAINT tenants_currency_check CHECK (currency IN ('QAR', 'INR'));
