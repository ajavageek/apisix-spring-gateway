INSERT INTO WAREHOUSE(ID, CITY, STATE)
VALUES
    (1, 'Popejoy', 'Iowa'),
    (2, 'Hooker', 'Oklahoma'),
    (3, 'China', 'Texas');

INSERT INTO STOCK(PRODUCT_ID, WAREHOUSE_ID, LEVEL)
VALUES
    (1, 1, 2),
    (1, 2, 3),
    (2, 1, 3),
    (2, 2, 4),
    (3, 1, 4),
    (3, 3, 6);