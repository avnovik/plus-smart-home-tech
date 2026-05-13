CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    username VARCHAR NOT NULL,
    state VARCHAR NOT NULL,
    shopping_cart_id UUID,
    country VARCHAR,
    city VARCHAR,
    street VARCHAR,
    house VARCHAR,
    flat VARCHAR,
    payment_id UUID,
    delivery_id UUID,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price NUMERIC,
    delivery_price NUMERIC,
    product_price NUMERIC
);

CREATE TABLE IF NOT EXISTS order_items (
    order_id UUID NOT NULL REFERENCES orders (id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);
