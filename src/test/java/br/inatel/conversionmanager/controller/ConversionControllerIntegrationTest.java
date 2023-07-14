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

    @Autowired
    private WebTestClient webTestClient;

    private static MockWebServer server;

    @BeforeAll
    public static void setup() throws Exception {
        server = new MockWebServer();
        server.start(InetAddress.getByName("localhost"), 8081);
    }

    @Test
    void givenValidConversionDto_whenSaveConversion_thenReturnCreatedStatusAndSavedConversion() {
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
    void givenInvalidConversionDto_whenSaveConversion_thenReturnNotFoundStatusAndErrorMessage() {
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


    //    @MockBean
//    private ConversionAdapter conversionAdapter;
//
//    @MockBean
//    private ConversionService conversionService;
//
//    @MockBean
//    private ConversionRepository conversionRepository;


    //    private ConversionDto createConversionDto(Float amount, String to, LocalDate date, Float converted) {
//        return ConversionDto.builder()
//                .amount(amount)
//                .to(to)
//                .convertedAmount(converted)
//                .date(date)
//                .build();
//    }
//
//    private Conversion createConversion(Float amount, String tocurrency, LocalDate date, Float converted) {
//        return Conversion.builder()
//                .amount(amount)
//                .base("EURO")
//                .tocurrency(tocurrency)
//                .date(date)
//                .converted(converted)
//                .build();
//
//    }

//    @Test
//    void givenExchangeRatesExist_whenGetAllQuotes_thenReturnExchangeRates() {
//        LocalDate currentDate = LocalDate.now();
//        List<Conversion> conversionList = new ArrayList<>();
//        conversionList.add(createConversion(500F, "USD", currentDate, 600F));
//        conversionList.add(createConversion(800F, "GBP", currentDate, 900F));
//
//        List<ConversionDto> conversionDtoList = conversionList.stream()
//                .map(conversion -> ConversionDto.builder()
//                        .amount(conversion.getAmount())
//                        .to(conversion.getTocurrency())
//                        .convertedAmount(conversion.getConverted())
//                        .date(conversion.getDate())
//                        .build())
//                .collect(Collectors.toList());
//
//        Page<ConversionDto> page = new PageImpl<>(conversionDtoList);
//
//        when(conversionService.getAllConversions(any(Pageable.class))).thenReturn(page);
//
//        webTestClient.get().uri("/api/exchange-rates")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.content").isArray()
//                .jsonPath("$.content[0].amount").isEqualTo(500F)
//                .jsonPath("$.content[0].to").isEqualTo("USD")
//                .jsonPath("$.content[0].convertedAmount").isEqualTo(600F)
//                .jsonPath("$.content[1].amount").isEqualTo(800F)
//                .jsonPath("$.content[1].to").isEqualTo("GBP")
//                .jsonPath("$.content[1].convertedAmount").isEqualTo(900F);
//    }
//
//    @Test
//    void givenValidCurrency_whenGetConversionsByCurrency_thenReturnConversions() {
//        String toCurrency = "GBP";
//        List<ConversionDto> expectedConversions = new ArrayList<>();
//        expectedConversions.add(createConversionDto(800F, "GBP", LocalDate.now(), 685.334F));
//        expectedConversions.add(createConversionDto(1000F, "GBP", LocalDate.now(), 854.417F));
//
//        when(conversionService.getConversionsByCurrency(toCurrency)).thenReturn(expectedConversions);
//
//        webTestClient.get().uri("/api/exchange-rates/{tocurrency}", toCurrency)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$").isArray()
//                .jsonPath("$[0].amount").isEqualTo(800F)
//                .jsonPath("$[0].to").isEqualTo("GBP")
//                .jsonPath("$[0].convertedAmount").isEqualTo(685.334F)
//                .jsonPath("$[1].amount").isEqualTo(1000F)
//                .jsonPath("$[1].to").isEqualTo("GBP")
//                .jsonPath("$[1].convertedAmount").isEqualTo(854.417F);
//    }
//
//    @Test
//    void givenValidConversionDto_whenSaveConversion_thenReturnCreatedStatusAndSavedConversion() {
//        ConversionDto conversionDto = ConversionDto.builder()
//                .amount(500F)
//                .to("USD")
//                .convertedAmount(600F)
//                .build();
//
//        ConversionDto savedConversion = ConversionDto.builder()
//                .amount(500F)
//                .to("USD")
//                .convertedAmount(600F)
//                .build();
//
//        when(conversionService.saveConversion(any(ConversionDto.class))).thenReturn(savedConversion);
//
//        webTestClient.post().uri("/api/exchange-rates")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(conversionDto)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(ConversionDto.class)
//                .isEqualTo(savedConversion);
//    }
//
//    @Test
//    void givenExchangeRatesExist_whenGetExchangeRates_thenReturnExchangeRates() {
//        ExchangeRateResponse exchangeRate = new ExchangeRateResponse(
//                1687996799L,
//                "EUR",
//                true,
//                Map.of(
//                        "ANG", 1.968256f,
//                        "SVC", 9.555569f,
//                        "CAD", 1.44658f,
//                        "XCD", 2.949964f,
//                        "USD", 1.091548f
//                ),
//                "2023-06-28",
//                true
//        );
//
//        List<ExchangeRateResponse> expectedExchangeRates = Collections.singletonList(exchangeRate);
//
//        when(conversionAdapter.getExchangeRates()).thenReturn(expectedExchangeRates);
//
//        webTestClient.get().uri("/api/exchange-rates/all")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(ExchangeRateResponse.class)
//                .isEqualTo(expectedExchangeRates);
//    }
//
//
//    @Test
//    void givenNoExchangeRatesExist_whenGetExchangeRates_thenReturnNoContent() {
//
//        List<ExchangeRateResponse> emptyExchangeRates = new ArrayList<>();
//
//        when(conversionAdapter.getExchangeRates()).thenReturn(emptyExchangeRates);
//
//        webTestClient.get().uri("/api/exchange-rates/all")
//                .exchange()
//                .expectStatus().isNoContent();
//    }
//
//    @Test
//    void givenConversionsExist_whenClearDatabase_thenDatabaseShouldBeEmpty() {
//        Conversion conversion1 = new Conversion();
//        conversion1.setAmount(500F);
//        conversion1.setTocurrency("USD");
//        conversion1.setDate(LocalDate.now());
//        conversion1.setConverted(600F);
//
//        Conversion conversion2 = new Conversion();
//        conversion2.setAmount(800F);
//        conversion2.setTocurrency("GBP");
//        conversion2.setDate(LocalDate.now());
//        conversion2.setConverted(900F);
//
//
//        webTestClient.delete().uri("/api/exchange-rates/clear-database")
//                .exchange()
//                .expectStatus().isOk();
//
//        List<Conversion> conversions = conversionRepository.findAll();
//        assertTrue(conversions.isEmpty());
//    }


