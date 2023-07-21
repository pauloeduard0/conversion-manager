CREATE DATABASE IF NOT EXISTS conversiondb;
USE conversiondb;

CREATE TABLE conversion_coin (
    id BINARY(16) NOT NULL,
    base VARCHAR(255) NOT NULL,
    amount DECIMAL(23, 5) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    converted DECIMAL(23, 5) NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (id)
);

CREATE DATABASE IF NOT EXISTS conversiondb_test;
USE conversiondb_test;

CREATE TABLE conversion_coin (
    id BINARY(16) NOT NULL,
    base VARCHAR(255) NOT NULL,
    amount DECIMAL(23, 5) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    converted DECIMAL(23, 5) NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (id)
);