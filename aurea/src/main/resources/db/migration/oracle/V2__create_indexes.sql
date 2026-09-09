CREATE INDEX idx_products_category
    ON products (category_id);

CREATE INDEX idx_order_items_order
    ON order_items (order_id);

CREATE INDEX idx_order_items_product
    ON order_items (product_id);

CREATE INDEX idx_order_items_product_price
    ON order_items (product_price_id);