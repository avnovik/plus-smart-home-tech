CREATE TABLE IF NOT EXISTS deliveries (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    state VARCHAR NOT NULL,

    from_country VARCHAR NOT NULL,
    from_city VARCHAR NOT NULL,
    from_street VARCHAR NOT NULL,
    from_house VARCHAR NOT NULL,
    from_flat VARCHAR NOT NULL,

    to_country VARCHAR NOT NULL,
    to_city VARCHAR NOT NULL,
    to_street VARCHAR NOT NULL,
    to_house VARCHAR NOT NULL,
    to_flat VARCHAR NOT NULL
);
