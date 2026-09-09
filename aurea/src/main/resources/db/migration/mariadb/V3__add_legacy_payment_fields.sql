ALTER TABLE orders
    ADD COLUMN legacy_payment_status VARCHAR(30) NULL;

ALTER TABLE orders
    ADD COLUMN legacy_payment_method VARCHAR(100) NULL;