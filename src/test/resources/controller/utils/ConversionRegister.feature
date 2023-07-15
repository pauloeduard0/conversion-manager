Feature: Register a new conversion in the collection and returns its information

  Background:
    * url 'http://localhost:8090'
    * path '/api/exchange-rates'
    * def jsonRequestConversion = read('classpath:controller/requests/request-conversion-create.json')

  Scenario: Create a conversion
    * replace jsonRequestConversion
      | token  | value  |
      | amount | amount |
      | to     | to
    Given request jsonRequestConversion
    And header Content-Type = 'application/json'
    When method POST