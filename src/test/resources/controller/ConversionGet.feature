Feature: Testing GET ConversionController

  Background:
    * url 'http://localhost:8090'

    Given path '/api/exchange-rates/clear-database'
    And method DELETE

  Scenario: Conversion successfully created with a valid currency
    Given path '/api/exchange-rates'
    And request { amount: 800, to: 'GBP' }
    When method POST
    Then status 201
    And def createdConversion = response

    Given path '/api/exchange-rates'
    When method GET
    Then status 200
    And assert response != null
    And assert responseStatus == 200
    And def actualConversion = response.content[0]
    And def expectedConversion = createdConversion
    And eval karate.match(expectedConversion, actualConversion)
    And match actualConversion.baseCurrency == expectedConversion.baseCurrency
    And match actualConversion.amount == expectedConversion.amount
    And match actualConversion.to == expectedConversion.to
    And def roundedActualAmount = karate.eval('Math.round(' + actualConversion.convertedAmount + ' * 100) / 100')
    And def roundedExpectedAmount = karate.eval('Math.round(' + expectedConversion.convertedAmount + ' * 100) / 100')
    And match roundedActualAmount == roundedExpectedAmount
    And match actualConversion.date == expectedConversion.date



