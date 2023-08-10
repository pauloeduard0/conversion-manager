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

- POST  http://localhost:8080/conversion

Request body:

```
{
    "amount": 500,
    "to": "USD"
}
```

To retrieve all conversions from Conversion Manager, use the following endpoint:

- GET  http://localhost:8080/conversion

Response body:

```
[
    {
        "id": "4517fea1-20dc-4505-bf54-888deaed7764",
        "baseCurrency": "EURO",
        "amount": 600.00000,
        "to": "CAD",
        "convertedAmount": 885.08760,
        "date": "2023-08-10"
    },
    {
        "id": "ad27fb2a-5ab2-434d-962e-cf5206971a86",
        "baseCurrency": "EURO",
        "amount": 500.00000,
        "to": "USD",
        "convertedAmount": 549.52600,
        "date": "2023-08-10"
    },
    {
        "id": "fa8f37a6-068b-4297-b163-9fffebc68c94",
        "baseCurrency": "EURO",
        "amount": 1000,
        "to": "USD",
        "convertedAmount": 1099.05200,
        "date": "2023-08-10"
    }
]
```

To retrieve conversions for a specific currency, use the following endpoint:

- GET  http://localhost:8080/conversion?currency=USD

Response body:

```
[
    {
        "id": "c2284079-5bb5-40ff-bfc6-27009b6f1f70",
        "baseCurrency": "EURO",
        "amount": 500.00000,
        "to": "USD",
        "convertedAmount": 549.31450,
        "date": "2023-08-10"
    },
    {
        "id": "fa8f37a6-068b-4297-b163-9fffebc68c94",
        "baseCurrency": "EURO",
        "amount": 1000.00000,
        "to": "USD",
        "convertedAmount": 1099.05200,
        "date": "2023-08-10"
    }
]
```

To retrieve conversions for a specific ID, use the following endpoint (id example):

- GET  http://localhost:8080/conversion?id=c2284079-5bb5-40ff-bfc6-27009b6f1f70

Response body:

```
{
    "id": "c2284079-5bb5-40ff-bfc6-27009b6f1f70",
    "baseCurrency": "EURO",
    "amount": 500.00000,
    "to": "USD",
    "convertedAmount": 549.31450,
    "date": "2023-08-10"
}
```