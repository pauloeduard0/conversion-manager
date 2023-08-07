Feature: Testing GET ConversionController Endpoints

  Background:
    * url 'http://localhost:8090'
    * def convertedCreated = callonce read('classpath:controller/tests/ConversionPost.feature@CreateCoin')
    * path '/conversion'

  Scenario: Retrieve all created conversions and check status code 200, amount of conversions and data content
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