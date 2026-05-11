CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN NOT NULL,
    weight BIGINT NOT NULL,
    width BIGINT NOT NULL,
    height BIGINT NOT NULL,
    depth BIGINT NOT NULL,
    quantity BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_bookings (
    order_id UUID PRIMARY KEY,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS order_booking_items (
    order_id UUID NOT NULL REFERENCES order_bookings (order_id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);
