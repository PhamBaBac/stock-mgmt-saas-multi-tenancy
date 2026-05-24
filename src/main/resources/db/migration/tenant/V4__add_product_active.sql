-- Add active status column to products table
ALTER TABLE products ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
