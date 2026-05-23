-- Drop the old unique constraints if they exist
ALTER TABLE categories DROP CONSTRAINT IF EXISTS uk_category_name;
ALTER TABLE products DROP CONSTRAINT IF EXISTS uk_product_reference;

-- Create partial unique indexes (only for active records)
CREATE UNIQUE INDEX IF NOT EXISTS uk_category_name_active ON categories (name) WHERE (deleted = false);
CREATE UNIQUE INDEX IF NOT EXISTS uk_product_reference_active ON products (reference) WHERE (deleted = false);
