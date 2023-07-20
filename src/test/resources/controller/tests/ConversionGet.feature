Feature: Testing GET ConversionController Endpoints

  Background:
    * url 'http://localhost:8090'
    * def convertedCreated = callonce read('classpath:controller/tests/ConversionPost.feature@CreateCoin')
    * configure afterFeature = function(){karate.call('../utils/ConversionDelete.feature');}

  Scenario: Retrieve all created conversions and check status code 200, amount of conversions and data content
    Given path '/api/exchange-rates'
    When method GET
    Then status 200
    And assert responseStatus == 200
    And match karate.sizeOf(response.content) == 4
    And def expectedBaseCurrencies = ['EURO', 'EURO', 'EURO', 'EURO']
    And def expectedAmounts = [500, 600, 700, 800]
    And def expectedTos = ['USD', 'GBP', 'JPY', 'EUR']
    And def actualBaseCurrencies = karate.map(response.content, function(item){ return item.baseCurrency })
    And def actualAmounts = karate.map(response.content, function(item){ return item.amount })
    And def actualTos = karate.map(response.content, function(item){ return item.to })
    And match actualBaseCurrencies == expectedBaseCurrencies
    And match karate.sort(actualAmounts) == karate.sort(expectedAmounts)
    And match karate.sort(actualTos) == karate.sort(expectedTos)

  Scenario Outline: Retrieve <currency> conversion should status code 200, amount of conversions and data content
    Given path '/api/exchange-rates/<currency>'
    When method GET
    Then status 200
    And assert responseStatus == 200
    And match karate.sizeOf(response) == 1
    And match response[*].to contains '<currency>'
    And match response[*].date contains '#regex \\d{4}-\\d{2}-\\d{2}'
    Examples:
      | currency |
      | USD      |
      | GBP      |
      | JPY      |
      | EUR      |

  Scenario Outline: Retrieve <currency> conversion should status code 200, no conversions and no data
    Given path '/api/exchange-rates/<currency>'
    When method GET
    Then status 200
    And assert responseStatus == 200
    And match karate.sizeOf(response) == 0
    Examples:
      | currency |
      | YYY      |
      | KKK      |
      | 555      |
      | @@@      |

  Scenario: Retrieve all API conversions with EURO Base, should return success equal to TRUE
    Given path '/api/exchange-rates/all'
    When method GET
    Then status 200
    And assert responseStatus == 200

    And karate.sizeOf(response.content) > 0
    And assert response[0].timestamp != null
    And assert response[0].base == "EUR"
    And assert response[0].success == true
    And assert response[0].rates != null
    And assert response[0].date != null
    And assert response[0].historical != null