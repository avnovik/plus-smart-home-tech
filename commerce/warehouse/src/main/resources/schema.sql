CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN NOT NULL,
    weight BIGINT NOT NULL,
    width BIGINT NOT NULL,
    height BIGINT NOT NULL,
    depth BIGINT NOT NULL,
    quantity BIGINT NOT NULL
);
