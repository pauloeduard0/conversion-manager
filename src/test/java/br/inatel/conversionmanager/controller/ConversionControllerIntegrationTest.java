package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ErrorDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    void givenExchangeRatesExist_whenGetExchangeRates_thenReturnOkStatusAndExchangeRatesList() throws JsonProcessingException {
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse(1689338403, "EUR", true,
                Map.of("ANG", BigDecimal.valueOf(2.019441),
                        "SVC", BigDecimal.valueOf(9.804953),
                        "CAD", BigDecimal.valueOf(1.471587),
                        "XCD", BigDecimal.valueOf(3.030053),
                        "MVR", BigDecimal.valueOf(17.277676),
                        "HRK", BigDecimal.valueOf(7.538677),
                        "AUD", BigDecimal.valueOf(1.635403)),
                "2023-07-14", true);

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(ExchangeRateResponse.class, new MyJsonComponent.ExchangeRateResponseSerializer());
        mapper.registerModule(javaTimeModule);

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(List.of(exchangeRateResponse)));

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
    void givenValidRequest_whenSaveConversion_thenReturnCreatedStatusAndSavedConversion() throws JsonProcessingException {
        UUID id = UUID.fromString("24bd7ae7-25ab-45f7-9e94-37c72d62cbe0");
        ConversionDto conversionDto = new ConversionDto(
                id, "EURO", BigDecimal.valueOf(800.0), "USD", BigDecimal.valueOf(896.9464), LocalDate.parse("2023-07-14")
        );

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(ConversionDto.class, new MyJsonComponent.ConversionDtoSerializer());
        mapper.registerModule(javaTimeModule);

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(conversionDto));

        server.enqueue(mockResponse);

        webTestClient.post().uri(server.url("/api/exchange-rates").uri())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"amount\": 800, \"to\": \"USD\" }")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("24bd7ae7-25ab-45f7-9e94-37c72d62cbe0")
                .jsonPath("$.baseCurrency").isEqualTo("EURO")
                .jsonPath("$.amount").isEqualTo(800.0)
                .jsonPath("$.to").isEqualTo("USD")
                .jsonPath("$.convertedAmount").isEqualTo(896.9464)
                .jsonPath("$.date").isEqualTo("2023-07-14");
    }

    @Test
    void givenInvalidRequest_whenSaveConversion_thenReturnNotFoundStatusAndErrorMessage() throws JsonProcessingException {
        ErrorDto errorDto = new ErrorDto(
                "https://api.api.conversionmanager.com/errors/not-found",
                "Currency Not Found for Registration",
                404,
                "Currency with 'KKK' was not found.",
                "/api/exchange-rates"
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new SimpleModule()
                .addSerializer(ErrorDto.class, new MyJsonComponent.ErrorDtoSerializer())
                .addDeserializer(ErrorDto.class, new MyJsonComponent.ErrorDtoDeserializer()));

        String errorJson = mapper.writeValueAsString(errorDto);

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorJson);

        server.enqueue(mockResponse);

        webTestClient.post().uri(server.url("/api/exchange-rates").uri())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"amount\": 800, \"to\": \"KKK\" }")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDto.class)
                .consumeWith(response -> {
                    ErrorDto returnedError = response.getResponseBody();
                    assert returnedError != null;
                    Assertions.assertEquals("https://api.api.conversionmanager.com/errors/not-found", returnedError.type());
                    Assertions.assertEquals("Currency Not Found for Registration", returnedError.title());
                    Assertions.assertEquals(404, returnedError.status());
                    Assertions.assertEquals("Currency with 'KKK' was not found.", returnedError.detail());
                    Assertions.assertEquals("/api/exchange-rates", returnedError.instance());
                });
    }

    @Test
    void givenInvalidRequest_whenSaveConversion_thenReturnBadRequestStatusAndErrorMessage() throws JsonProcessingException {
        ErrorDto errorDto = new ErrorDto(
                "https://api.conversionmanager.com/errors/bad-request",
                "Invalid Format Exception",
                400,
                "Cannot deserialize value of type that is not a valid `Double` value",
                "/api/exchange-rates"
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new SimpleModule()
                .addSerializer(ErrorDto.class, new MyJsonComponent.ErrorDtoSerializer())
                .addDeserializer(ErrorDto.class, new MyJsonComponent.ErrorDtoDeserializer()));

        String errorJson = mapper.writeValueAsString(errorDto);

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorJson);

        server.enqueue(mockResponse);

        webTestClient.post().uri(server.url("/api/exchange-rates").uri())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"amount\": \"invalid\", \"to\": \"USD\" }")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorDto.class)
                .consumeWith(response -> {
                    ErrorDto returnedError = response.getResponseBody();
                    assert returnedError != null;
                    Assertions.assertEquals("https://api.conversionmanager.com/errors/bad-request", returnedError.type());
                    Assertions.assertEquals("Invalid Format Exception", returnedError.title());
                    Assertions.assertEquals(400, returnedError.status());
                    Assertions.assertEquals("Cannot deserialize value of type that is not a valid `Double` value", returnedError.detail());
                    Assertions.assertEquals("/api/exchange-rates", returnedError.instance());
                });
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
}