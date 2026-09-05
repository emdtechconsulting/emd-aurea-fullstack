CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT(1) NOT NULL,
    display_order INT DEFAULT NULL,
    name VARCHAR(80) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY UKt8o6pivur7nn124jehx7cygw5 (name)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    name VARCHAR(200) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    category_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    KEY FKog2rp4qthbtt2lfyhfo32lsw9 (category_id),

    CONSTRAINT FKog2rp4qthbtt2lfyhfo32lsw9
        FOREIGN KEY (category_id)
        REFERENCES categories (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE product_prices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT(1) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    product_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY uk_product_quantity (
        product_id,
        quantity
    ),

    CONSTRAINT FKo21ew0lemtpkoyly3vm1mq925
        FOREIGN KEY (product_id)
        REFERENCES products (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    address VARCHAR(250) DEFAULT NULL,
    created_at DATETIME(6) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    delivery_date DATE DEFAULT NULL,
    delivery_fee DECIMAL(10,2) NOT NULL,
    delivery_time TIME DEFAULT NULL,
    district VARCHAR(100) DEFAULT NULL,
    observations VARCHAR(1000) DEFAULT NULL,
    products_subtotal DECIMAL(10,2) NOT NULL,
    requires_delivery BIT(1) NOT NULL,
    source ENUM(
        'FIREBASE_MIGRATION',
        'MANUAL',
        'MOBILE',
        'WEB'
    ) NOT NULL,
    status ENUM(
        'CANCELLED',
        'CONFIRMED',
        'DELIVERED',
        'DRAFT',
        'PREPARING'
    ) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE order_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    description_snapshot VARCHAR(250) NOT NULL,
    item_type ENUM(
        'CATALOG',
        'MANUAL'
    ) NOT NULL,
    notes VARCHAR(500) DEFAULT NULL,
    price_type ENUM(
        'PACKAGE',
        'UNIT'
    ) NOT NULL,
    quantity INT NOT NULL,
    reference_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT DEFAULT NULL,
    product_price_id BIGINT DEFAULT NULL,

    PRIMARY KEY (id),

    KEY FKbioxgbv59vetrxe0ejfubep1w (order_id),
    KEY FKocimc7dtr037rh4ls4l95nlfi (product_id),
    KEY FKqogy9l5hk5sk4umb7q5swuej4 (product_price_id),

    CONSTRAINT FKbioxgbv59vetrxe0ejfubep1w
        FOREIGN KEY (order_id)
        REFERENCES orders (id),

    CONSTRAINT FKocimc7dtr037rh4ls4l95nlfi
        FOREIGN KEY (product_id)
        REFERENCES products (id),

    CONSTRAINT FKqogy9l5hk5sk4umb7q5swuej4
        FOREIGN KEY (product_price_id)
        REFERENCES product_prices (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE app_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    enabled BIT(1) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM(
        'ADMIN',
        'DELIVERY',
        'PRODUCCION',
        'VENTAS'
    ) NOT NULL,
    username VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY UKspsnwr241e9k9c8p5xl4k45ih (
        username
    )
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;