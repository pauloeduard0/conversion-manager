Feature: Delete

  Background:
    * url 'http://localhost:8090'

  Scenario: Conversion successfully created with a valid currency
    Given path '/api/exchange-rates/clear-database'
    And method DELETE
    Then status 200
    And assert response != null
    And assert responseStatus == 200
    And karate.sizeOf(response.content) == 0

  Scenario: Successfully delete conversion cache
    Given path '/currencycache'
    And method DELETE
    Then status 200
    Given path '/api/exchange-rates/all'
    When method GET
    Then status 200
    And assert response != null
    And assert responseStatus == 200
