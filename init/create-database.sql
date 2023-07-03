CREATE DATABASE IF NOT EXISTS conversiondb;
USE conversiondb;

CREATE TABLE IF NOT EXISTS conversion_coin (
id BINARY(16) NOT NULL,
amount FLOAT NOT NULL,
baseCurrency VARCHAR(255) DEFAULT 'EURO',
toCurrency VARCHAR(255) NOT NULL,
conversionDate DATE NOT NULL,
PRIMARY KEY (id)
);