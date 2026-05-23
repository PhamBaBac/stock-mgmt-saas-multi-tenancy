-- Migration for Tenant-Specific Schema
-- This script runs for EACH company schema

CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT uk_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(255) NOT NULL,
    reference VARCHAR(255) NOT NULL,
    description TEXT,
    alert_threshold INTEGER NOT NULL DEFAULT 0,
    price DECIMAL(19, 2) NOT NULL,
    available_quantity INTEGER NOT NULL DEFAULT 0,
    category_id VARCHAR(36),
    CONSTRAINT uk_product_reference UNIQUE (reference),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS stock_mvts (
    id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    date_mvt DATE NOT NULL,
    quantity INTEGER NOT NULL,
    type_mvt VARCHAR(20) NOT NULL,
    comment TEXT,
    product_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_stock_mvt_product FOREIGN KEY (product_id) REFERENCES products(id)
);
