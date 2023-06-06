CREATE TABLE IF NOT EXISTS WAREHOUSE (
    ID BIGINT PRIMARY KEY NOT NULL,
    CITY VARCHAR(50) NOT NULL,
    STATE VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS STOCK (
   PRODUCT_ID BIGINT NOT NULL,
   WAREHOUSE_ID BIGINT NOT NULL,
   LEVEL INT NOT NULL,
   FOREIGN KEY (WAREHOUSE_ID) REFERENCES WAREHOUSE(ID)
)