CREATE SCHEMA pricing;

CREATE TABLE pricing.price (
    product_id BIGINT PRIMARY KEY NOT NULL,
    price NUMERIC(10,2) NOT NULL
);

INSERT INTO pricing.price(product_id, price)
VALUES
    (1, 0.49),
    (2, 1.49),
    (3, 9.99);
