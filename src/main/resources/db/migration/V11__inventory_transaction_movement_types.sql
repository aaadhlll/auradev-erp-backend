-- Distinguish manual inventory operations in the immutable stock ledger.
ALTER TYPE movement_type ADD VALUE IF NOT EXISTS 'opening_stock';
ALTER TYPE movement_type ADD VALUE IF NOT EXISTS 'stock_receipt';
ALTER TYPE movement_type ADD VALUE IF NOT EXISTS 'count_in';
ALTER TYPE movement_type ADD VALUE IF NOT EXISTS 'count_out';
