Feature: Testing Post ConversionController

  Background:
    * url 'http://localhost:8090'
    * def convertConfig = { amount: 800, to: 'GBP' }
    * def convertRegistered = callonce read('classpath:controller/utils/ConversionRegister.feature') convertConfig
    * configure afterFeature = function(){karate.call('controller/tests/ConversionDelete.feature');}
    * def jsonRequestConversion = read('classpath:controller/requests/request-conversion-create.json')

  @CreateCoin
  Scenario Outline: Conversion successfully created with a valid currency

    * replace jsonRequestConversion
      | token  | value  |
      | amount | amount |
      | to     | to     |
    Given request jsonRequestConversion
    And header Content-Type = 'application/json'
    Given path '/api/exchange-rates'
    When method POST
    Then status 201
    And assert response != null
    And assert responseStatus == 201
    Examples:
      | amount  | to  |
      | 500     | USD |
      | 600     | GBP |
      | 700     | JPY |
      | 800     | EUR |

  Scenario Outline: Conversion successfully created with a valid currency

    * replace jsonRequestConversion
      | token  | value  |
      | amount | amount |
      | to     | to     |
    Given request jsonRequestConversion
    And header Content-Type = 'application/json'
    Given path '/api/exchange-rates'
    When method POST
    Then status 400
    And assert response != null
    And assert responseStatus == 400
    And match response == {type: 'https://api.conversionmanager.com/errors/bad-request', title: 'Invalid Format Exception', status: 400, detail: "Cannot deserialize value of type that is not a valid `Float` value",instance: '/api/exchange-rates'}
    Examples:
      | amount  | to  |
      | invalid | USD |
      | ssd     | GBP |
      | @@@     | JPY |
      | KKK     | EUR |

  Scenario Outline: Conversion successfully created with a valid currency

    * replace jsonRequestConversion
      | token  | value  |
      | amount | amount |
      | to     | to     |
    Given request jsonRequestConversion
    And header Content-Type = 'application/json'
    Given path '/api/exchange-rates'
    When method POST
    Then status 404
    And assert response != null
    And assert responseStatus == 404
    And match response == {type: 'https://api.api.conversionmanager.com/errors/not-found', title: 'Currency Not Found for Registration', status: 404, detail: "Currency with '<to>' was not found.", instance: '/api/exchange-rates'}
    Examples:
      | amount | to  |
      | 500    | UKK |
      | 400    | UJJ |
      | 300    | USB |
      | 200    | UST |












