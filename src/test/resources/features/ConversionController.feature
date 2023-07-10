Feature: Testing ConversionController

  Background:
    * url 'http://localhost:8080'
    * def convertExpected =
    """
{
    "baseCurrency": "EURO",
    "amount": 800.0,
    "to": "GBP",
    "convertedAmount": 684.5656,
    "date": "2023-07-10"
}
    """

  Scenario: Conversion successfully created with a valid corrency
    Given path '/api/exchange-rates'
    And request { amount : 800, to: 'GBP' }
    When method POST
    Then status 201
    And assert response != null
    And assert responseStatus == 201
