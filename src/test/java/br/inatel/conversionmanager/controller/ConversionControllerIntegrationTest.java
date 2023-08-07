package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ErrorDto;
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

        webTestClient.post().uri(server.url("/conversion").uri())
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
                "https://api.conversionmanager.com/errors/not-found",
                "Currency Not Found for Registration",
                404,
                "Currency with 'KKK' was not found.",
                "/conversion"
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

        webTestClient.post().uri(server.url("/conversion").uri())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"amount\": 800, \"to\": \"KKK\" }")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDto.class)
                .consumeWith(response -> {
                    ErrorDto returnedError = response.getResponseBody();
                    assert returnedError != null;
                    Assertions.assertEquals("https://api.conversionmanager.com/errors/not-found", returnedError.type());
                    Assertions.assertEquals("Currency Not Found for Registration", returnedError.title());
                    Assertions.assertEquals(404, returnedError.status());
                    Assertions.assertEquals("Currency with 'KKK' was not found.", returnedError.detail());
                    Assertions.assertEquals("/conversion", returnedError.instance());
                });
    }

    @Test
    void givenInvalidRequest_whenSaveConversion_thenReturnBadRequestStatusAndErrorMessage() throws JsonProcessingException {
        ErrorDto errorDto = new ErrorDto(
                "https://api.conversionmanager.com/errors/bad-request",
                "Invalid Format Exception",
                400,
                "Cannot deserialize value of type that is not a valid `Double` value",
                "/conversion"
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

        webTestClient.post().uri(server.url("/conversion").uri())
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
                    Assertions.assertEquals("/conversion", returnedError.instance());
                });
    }
}