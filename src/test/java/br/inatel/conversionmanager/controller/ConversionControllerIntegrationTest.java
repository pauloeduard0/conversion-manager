package br.inatel.conversionmanager.controller;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.InetAddress;

@SpringJUnitConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("h2-test")
class ConversionControllerIntegrationTest {

    private static MockWebServer server;
    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    public static void setup() throws Exception {
        server = new MockWebServer();
        server.start(InetAddress.getByName("localhost"), 8081);
    }

    @Test
    void givenValidRequest_whenSaveConversion_thenReturnCreatedStatusAndSavedConversion() {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("{ \"baseCurrency\": \"EURO\", \"amount\": 800.0, \"to\": \"USD\", \"convertedAmount\": 896.9464, \"date\": \"2023-07-14\" }");

        server.enqueue(mockResponse);

        webTestClient.post().uri(server.url("/api/exchange-rates").uri())
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("{ \"amount\": 800, \"to\": \"USD\" }")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.baseCurrency").isEqualTo("EURO")
                .jsonPath("$.amount").isEqualTo(800.0)
                .jsonPath("$.to").isEqualTo("USD")
                .jsonPath("$.convertedAmount").isEqualTo(896.9464)
                .jsonPath("$.date").isEqualTo("2023-07-14");
    }

    @Test
    void givenInvalidRequest_whenSaveConversion_thenReturnNotFoundStatusAndErrorMessage() {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("{ \"type\": \"https://api.api.conversionmanager.com/errors/not-found\", \"title\": \"Currency Not Found for Registration\", \"status\": 404, \"detail\": \"Currency with 'KKK' was not found.\", \"instance\": \"/api/exchange-rates\" }");

        server.enqueue(mockResponse);

        webTestClient.post().uri(server.url("/api/exchange-rates").uri())
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("{ \"amount\": 800, \"to\": \"KKK\" }")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.type").isEqualTo("https://api.api.conversionmanager.com/errors/not-found")
                .jsonPath("$.title").isEqualTo("Currency Not Found for Registration")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.detail").isEqualTo("Currency with 'KKK' was not found.")
                .jsonPath("$.instance").isEqualTo("/api/exchange-rates");
    }

    @Test
    void givenExchangeRatesExist_whenGetExchangeRates_thenReturnOkStatusAndExchangeRatesList() {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("[\n" +
                        "    {\n" +
                        "        \"timestamp\": 1689338403,\n" +
                        "        \"base\": \"EUR\",\n" +
                        "        \"success\": true,\n" +
                        "        \"rates\": {\n" +
                        "            \"ANG\": 2.019441,\n" +
                        "            \"SVC\": 9.804953,\n" +
                        "            \"CAD\": 1.471587,\n" +
                        "            \"XCD\": 3.030053,\n" +
                        "            \"MVR\": 17.277676,\n" +
                        "            \"HRK\": 7.538677,\n" +
                        "            \"AUD\": 1.635403\n" +
                        "        },\n" +
                        "        \"date\": \"2023-07-14\",\n" +
                        "        \"historical\": true\n" +
                        "    }\n" +
                        "]");

        server.enqueue(mockResponse);

        webTestClient.get().uri(server.url("/api/exchange-rates/all").uri())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].timestamp").isEqualTo(1689338403)
                .jsonPath("$[0].base").isEqualTo("EUR")
                .jsonPath("$[0].success").isEqualTo(true)
                .jsonPath("$[0].rates.ANG").isEqualTo(2.019441)
                .jsonPath("$[0].rates.SVC").isEqualTo(9.804953)
                .jsonPath("$[0].rates.CAD").isEqualTo(1.471587)
                .jsonPath("$[0].rates.XCD").isEqualTo(3.030053)
                .jsonPath("$[0].rates.MVR").isEqualTo(17.277676)
                .jsonPath("$[0].rates.HRK").isEqualTo(7.538677)
                .jsonPath("$[0].rates.AUD").isEqualTo(1.635403)
                .jsonPath("$[0].date").isEqualTo("2023-07-14")
                .jsonPath("$[0].historical").isEqualTo(true);
    }

    @Test
    void givenNoExchangeRatesExist_whenGetExchangeRates_thenReturnNoContentStatus() {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(204);

        server.enqueue(mockResponse);

        webTestClient.get().uri(server.url("/api/exchange-rates/all").uri())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void givenInvalidRequest_whenSaveConversion_thenReturnBadRequestStatusAndErrorMessage() {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("{ \"type\": \"https://api.conversionmanager.com/errors/bad-request\", \"title\": \"Invalid Format Exception\", \"status\": 400, \"detail\": \"Cannot deserialize value of type that is not a valid `Float` value\", \"instance\": \"/api/exchange-rates\" }");

        server.enqueue(mockResponse);

        webTestClient.post().uri(server.url("/api/exchange-rates").uri())
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("{ \"amount\": \"invalid\", \"to\": \"USD\" }")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.type").isEqualTo("https://api.conversionmanager.com/errors/bad-request")
                .jsonPath("$.title").isEqualTo("Invalid Format Exception")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.detail").isEqualTo("Cannot deserialize value of type that is not a valid `Float` value")
                .jsonPath("$.instance").isEqualTo("/api/exchange-rates");
    }
}