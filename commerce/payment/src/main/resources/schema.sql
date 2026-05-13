CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_total NUMERIC NOT NULL,
    delivery_total NUMERIC NOT NULL,
    fee_total NUMERIC NOT NULL,
    total_payment NUMERIC NOT NULL,
    status VARCHAR NOT NULL
);
