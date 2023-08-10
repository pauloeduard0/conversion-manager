# Conversion Manager REST Application

To set up and run the Conversion Manager REST application, please ensure you have the following prerequisites:

- Java 17
- Spring Boot
- Maven
- Insomnia or Postman
- Docker on Ubuntu
- Git
- IntelliJ IDEA

 ---

# Initialization

*Clone this repository.*

*Follow the step below to run the project using IntelliJ:*

- Build a Maven project and generate the artifacts in the terminal:

mvn clean install -DskipTests

---

*Run the following commands on Docker:*

- Start docker compose:

sudo service docker start

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
