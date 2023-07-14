Feature: Testing GET ConversionController

  Background:
    * url 'http://localhost:8090'
    * def convertedCreated = callonce read('classpath:controller/tests/ConversionPost.feature@CreateCoin')

  Scenario: Retrieve All Conversions
    Given path '/api/exchange-rates'
    When method GET
    Then status 200
    And assert response != null
    And assert responseStatus == 200
    And match karate.sizeOf(response.content) == 5
    And def expectedBaseCurrencies = ['EURO', 'EURO', 'EURO', 'EURO', 'EURO']
    And def expectedAmounts = [500, 600, 700, 800, 800]
    And def expectedTos = ['USD', 'GBP', 'JPY', 'EUR', 'GBP']
    And def actualBaseCurrencies = karate.map(response.content, function(item){ return item.baseCurrency })
    And def actualAmounts = karate.map(response.content, function(item){ return item.amount })
    And def actualTos = karate.map(response.content, function(item){ return item.to })

    And match actualBaseCurrencies == expectedBaseCurrencies
    And match karate.sort(actualAmounts) == karate.sort(expectedAmounts)
    And match karate.sort(actualTos) == karate.sort(expectedTos)


  Scenario: Retrieve Specific Currency USD Conversion
    Given path '/api/exchange-rates/USD'
    When method GET
    Then status 200
    And assert response != null
    And assert responseStatus == 200
    And match karate.sizeOf(response) == 1
    And match response[0].baseCurrency == 'EURO'
    And match response[0].amount == 500
    And match response[0].to == 'USD'
    And match response[0].date == '#regex \\d{4}-\\d{2}-\\d{2}'


  Scenario: Retrieve Specific Currency GBP Conversion
    Given path '/api/exchange-rates/GBP'
    When method GET
    Then status 200
    And assert response != null
    And assert responseStatus == 200
    And match karate.sizeOf(response) == 2
    And match response[*].to contains 'GBP'
    And match response[*].amount contains 600.0
    And match response[*].amount contains 800.0
    And match response[*].date contains '#regex \\d{4}-\\d{2}-\\d{2}'

  Scenario: Retrieve All Conversions
    Given path '/api/exchange-rates/all'
    When method GET
    Then status 200
    And assert response != null
    And assert responseStatus == 200

    And karate.sizeOf(response.content) > 0
    And assert response[0].timestamp != null
    And assert response[0].base != null
    And assert response[0].success != null
    And assert response[0].rates != null
    And assert response[0].date != null
    And assert response[0].historical != null


