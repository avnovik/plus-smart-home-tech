CREATE TABLE IF NOT EXISTS shopping_carts (
    id UUID PRIMARY KEY,
    username VARCHAR NOT NULL,
    state VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart_items (
    cart_id UUID NOT NULL REFERENCES shopping_carts (id),
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (cart_id, product_id)
);
