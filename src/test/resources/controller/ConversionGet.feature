Feature: Testing GET ConversionController

  Background:
    * url 'http://localhost:8080'

  Scenario: Conversion successfully created with a valid currency
    Given path '/api/exchange-rates'
    When method GET
    Then status 200
    And assert response != null
    And assert responseStatus == 200
