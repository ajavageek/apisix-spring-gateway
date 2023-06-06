CREATE SCHEMA stocks;

CREATE TABLE stocks.warehouse (
    id BIGINT PRIMARY KEY NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL
);

CREATE TABLE stocks.stock (
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    level INT NOT NULL,
    FOREIGN KEY (warehouse_id) REFERENCES stocks.warehouse(id)
);

INSERT INTO stocks.warehouse(id, city, state)
VALUES
        (1, 'Popejoy', 'Iowa'),
        (2, 'Hooker', 'Oklahoma'),
        (3, 'China', 'Texas'),
        (4, 'Rainbow City', 'Alabama'),
        (5, 'Blue Grass', 'Iowa'),
        (6, 'Pink', 'Oklahoma'),
        (7, 'Colon', 'Michigan'),
        (8, 'Cool', 'Texas'),
        (9, 'Oblong', 'Illinois'),
        (10, 'Speed', 'North Carolina'),
        (11, 'Last Chance', 'Iowa'),
        (12, 'Uncertain', 'Texas'),
        (13, 'Winnebago', 'Minnesota'),
        (14, 'Climax', 'Michigan'),
        (15, 'Three Way', 'Tennessee'),
        (16, 'Coward', 'South Carolina'),
        (17, 'Okay', 'Oklahoma'),
        (18, 'Atomic City', 'Idaho'),
        (19, 'Superior', 'Wyoming'),
        (20, 'Canadian', 'Texas');

INSERT INTO stocks.stock(product_id, warehouse_id, level)
VALUES
    (1, 1, 1),
    (1, 2, 2),
    (1, 3, 3),
    (1, 4, 4),
    (2, 10, 5),
    (2, 11, 6),
    (2, 12, 7),
    (2, 13, 8),
    (3, 17, 9),
    (3, 18, 10),
    (3, 19, 11),
    (3, 20, 12);
