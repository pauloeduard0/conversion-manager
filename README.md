# Conversion Manager REST Application

To set up and run the Conversion Manager REST application, please ensure you have the following prerequisites:

- Java 17
- Spring Boot
- Maven
- Insomnia or Postman
- Docker
- Git
- IntelliJ IDEA

 ---

# Initialization

*Clone this repository.*

Follow the instructions below to run the application using Docker:

- Create the network:

docker network create inatel

- Start the MySql database:

docker container run --name mysql --network=inatel -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=bootdb -p 3306:3306 -p 33060:33060 -d mysql


---

# Using Docker Compose to Run the Project

If you prefer using Docker Compose, the application will utilize the MySQL database image (Docker).

*Follow the steps below to run the project using IntelliJ:*

mvn clean install -DskipTest

*Run the following commands on Docker:*

- Create the network:

docker network create inatel

- Start docker compose:

docker compose up --build

---

# How to Use the API

To register a new currency conversion, use the following endpoint:

- POST  http://localhost:8080/api/exchange-rates

Request body:

```
{
    "amount": 800,
    "to": "USD"
}

```

To retrieve all conversions from Conversion Manager, use the following endpoint:

- GET  http://localhost:8080/api/exchange-rates

Response body:

```
[
    {
         "baseCurrency": "EURO",
         "amount": 800.0,
         "to": "GBP",
         "convertedAmount": 687.241,
         "date": "2023-07-14"
    },
    {
         "baseCurrency": "EURO",
         "amount": 800.0,
         "to": "JPY",
         "convertedAmount": 124788.82,
         "date": "2023-07-14"
    }
    {
         "baseCurrency": "EURO",
         "amount": 800.0,
         "to": "USD",
         "convertedAmount": 896.946,
         "date": "2023-07-14"
    }
]
```

To retrieve all conversion rates available from the API, use the following endpoint::

- GET  http://localhost:8080/api/exchange-rates/all

Response body:

```
[
    {
        "timestamp": 1689338403,
        "base": "EUR",
        "success": true,
        "rates": {
            "ANG": 2.019441,
            "SVC": 9.804953,
            "CAD": 1.471587,
            "XCD": 3.030053,
            "MVR": 17.277676,
            ...
            },
        "date": "2023-07-14",
        "historical": true
    }
]
```

To retrieve conversions for a specific currency, use the following endpoint:

- GET  http://localhost:8080/api/exchange-rates/USD

Response body:

```
[
    {
        "baseCurrency": "EURO",
        "amount": 800.0,
        "to": "USD",
        "convertedAmount": 896.946,
        "date": "2023-07-14"
    },
    {
        "baseCurrency": "EURO",
        "amount": 800.0,
        "to": "USD",
        "convertedAmount": 896.946,
        "date": "2023-07-14"
    }
]
```
