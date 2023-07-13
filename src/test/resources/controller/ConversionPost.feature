Feature: Testing Post ConversionController

  Background:
    * url 'http://localhost:8090'

    Given path '/api/exchange-rates/clear-database'
    And method DELETE

    * def convertExpected =
      """
      {
        baseCurrency: 'EURO',
        amount: 800.0,
        to: 'GBP',
        convertedAmount: '#regex .+',
        date: '#regex \\d{4}-\\d{2}-\\d{2}'
      }
      """

  Scenario: Conversion successfully created with a valid currency

    Given path '/api/exchange-rates'
    And request { amount: 800, to: 'GBP' }
    When method POST
    Then status 201
    And assert response != null
    And assert responseStatus == 201
    And def actualResponse = response
    And def expectedResponse = convertExpected
    And eval karate.match(expectedResponse, actualResponse)
    And match response.baseCurrency == 'EURO'
    And match response.amount == 800.0
    And match response.to == 'GBP'
    And match response.date == '#regex \\d{4}-\\d{2}-\\d{2}'













