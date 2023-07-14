Feature: Delete
  Background:
    * url 'http://localhost:8090'

  Scenario: Conversion successfully created with a valid currency

    Given path '/api/exchange-rates/clear-database'
    And method DELETE