Feature: Delete

  Background:
    * url 'http://localhost:8090'

  Scenario: Clears the database and returns check status code 204 with size equal to zero
    Given path '/api/exchange-rates/clear-database'
    And method DELETE
    Then status 204
    And assert response != null
    And assert responseStatus == 204
    And karate.sizeOf(response.content) == 0