BEGIN;

DROP TABLE IF EXISTS payment CASCADE;

CREATE TABLE payment(
                        paymentid SERIAL PRIMARY KEY,
                        orderid INT NOT NULL,
                        userid VARCHAR(255) NOT NULL,
                        status INT,
                        amount FLOAT,
                        issuedAt TIMESTAMP
);

COMMIT;