Feature: Testing GET ConversionController Endpoints

  Background:
    * url 'http://localhost:8090'
    * def convertedCreated = callonce read('classpath:controller/tests/ConversionPost.feature@CreateCoin')

  Scenario: Retrieve all created conversions and check status code 200, amount of conversions and data content
    Given path '/api/exchange-rates'
    When method GET
    Then status 200
    And assert responseStatus == 200
    And def conversionModel =
    """
    {
        "id": '#regex (?i)^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$',
        "baseCurrency": 'EURO',
        "amount": '#number',
        "to": '#string',
        "convertedAmount": '#number',
        "date": '#regex \\d{4}-\\d{2}-\\d{2}'
    }
    """
    And match response contains conversionModel


  Scenario Outline: Retrieve <currency> conversion should status code 200, amount of conversions and data content
    Given path '/api/exchange-rates'
    And params { currency: '<currency>' }
    When method GET
    Then status 200
    And assert responseStatus == 200
    And match response[*].to contains '<currency>'
    And match response[*].date contains '#regex \\d{4}-\\d{2}-\\d{2}'
    Examples:
      | currency |
      | USD      |
      | GBP      |
      | JPY      |
      | EUR      |

  Scenario Outline: Retrieve <currency> conversion should status code 200, no conversions and no data
    Given path '/api/exchange-rates'
    And params { currency: '<currency>' }
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