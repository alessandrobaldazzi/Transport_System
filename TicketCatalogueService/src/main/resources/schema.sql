BEGIN;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;

CREATE TABLE tickets (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         price FLOAT NOT NULL,
                         duration INT NULL,
                         zones VARCHAR(255) NOT NULL ,
                         type VARCHAR(255) NOT NULL ,
                         max_age INT,
                         min_age INT
);

CREATE TABLE users (
                       username VARCHAR(255) PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE orders(
                       id SERIAL PRIMARY KEY,
                       quantity INT NOT NULL,
                       ticketId INT REFERENCES tickets(id),
                       username VARCHAR(255) REFERENCES users(username),
                       status VARCHAR(255) NOT NULL
);

INSERT INTO users (username, email) VALUES ('customer', 'customer@email.it');
INSERT INTO tickets (name, price,duration, zones, type, max_age, min_age) VALUES ('promo_student',123, null, '1,2,3', 'YEARLY', 25, NULL);
INSERT INTO tickets (name, price,duration, zones, type, max_age, min_age) VALUES ('promo_elders',123, null,'1,2,3',  'YEARLY', NULL, 65);
INSERT INTO tickets (name, price, duration, zones, type, max_age, min_age) VALUES ('standard', 123, 3, '1,2,3', 'ORDINARY', null, null);

COMMIT;