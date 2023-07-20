Feature: Delete test database

  Background:
    * url 'http://localhost:8090'
    * path '/api/exchange-rates/clear-database'

  Scenario: Clear the database
    And method DELETE
