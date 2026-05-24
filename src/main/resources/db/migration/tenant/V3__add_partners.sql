CREATE TABLE IF NOT EXISTS partners (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    address TEXT,
    tax_id VARCHAR(255),
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

ALTER TABLE stock_mvts ADD COLUMN partner_id VARCHAR(255);

ALTER TABLE stock_mvts
    ADD CONSTRAINT fk_stock_mvts_partner
    FOREIGN KEY (partner_id) REFERENCES partners(id);
