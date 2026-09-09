ALTER TABLE orders
    ADD COLUMN firebase_document_id VARCHAR(128) NULL;

ALTER TABLE orders
    ADD COLUMN legacy_order_number INT NULL;

ALTER TABLE orders
    ADD COLUMN legacy_created_at TIMESTAMP NULL;

ALTER TABLE orders
    ADD COLUMN legacy_updated_at TIMESTAMP NULL;

CREATE UNIQUE INDEX uk_orders_firebase_document_id
    ON orders (firebase_document_id);