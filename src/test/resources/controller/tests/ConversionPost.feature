Feature: Testing POST ConversionController Endpoints

  Background:
    * url 'http://localhost:8090'
    * path '/conversion'
    * def jsonRequestConversion = read('classpath:controller/requests/request-conversion-create.json')
    * header Content-Type = 'application/json'

  @CreateCoin
  Scenario Outline: Create new conversions should return status code 201

    * replace jsonRequestConversion
      | token  | value  |
      | amount | amount |
      | to     | to     |
    Given request jsonRequestConversion
    When method POST
    Then status 201
    And assert responseStatus == 201
    And match response.id == '#regex (?i)^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$'
    And match response.baseCurrency == "EURO"
    And match response.amount == parseInt('<amount>')
    And match response.to == '<to>'
    And match response.date == '#regex \\d{4}-\\d{2}-\\d{2}'
    Examples:
      | amount | to  |
      | 500    | USD |
      | 600    | GBP |
      | 700    | JPY |
      | 800    | EUR |

  Scenario Outline: Create new conversions with invalid quantities should return status code 400

    * replace jsonRequestConversion
      | token  | value  |
      | amount | amount |
      | to     | to     |
    Given request jsonRequestConversion
    When method POST
    Then status 400
    And assert response != null
    And assert responseStatus == 400
    And match response == {type: 'https://api.conversionmanager.com/errors/bad-request', title: 'Invalid Format Exception', status: 400, detail: "Cannot deserialize value of type that is not a valid `Double` value", instance: '/conversion'}
    Examples:
      | amount  | to  |
      | invalid | USD |
      | ssd     | GBP |
      | @@@     | JPY |
      | KKK     | EUR |

  Scenario Outline: Create new conversions with non-existing currency should return status code 404

    * replace jsonRequestConversion
      | token  | value  |
      | amount | amount |
      | to     | to     |
    Given request jsonRequestConversion
    When method POST
    Then status 404
    And assert response != null
    And assert responseStatus == 404
    And match response == {type: 'https://api.conversionmanager.com/errors/not-found', title: 'Currency Not Found for Registration', status: 404, detail: "Currency with '<to>' was not found. Please check an existing currency for conversion", instance: '/conversion'}
    Examples:
      | amount | to  |
      | 500    | UKK |
      | 400    | UJJ |
      | 300    | USB |
      | 200    | UST |

  Scenario Outline: Create new conversions with empty to should return status code 400

    * def requestPayload = { "amount": "<amount>" }
    * replace requestPayload
    Given request requestPayload
    When method POST
    Then status 400
    And assert response != null
    And assert responseStatus == 400
    And match response == {type: 'https://api.conversionmanager.com/errors/bad-request', title: 'Method Argument Not Valid Exception', status: 400, detail: "The request contains an invalid argument. The field to or amount ,object is null, which violates the validation.", instance: '/conversion'}
    Examples:
      | amount |
      | 500    |
      | 400    |
      | 300    |

  Scenario Outline: Create new conversions with empty amount should return status code 400

    * def requestPayload = { "to": "<to>" }
    * replace requestPayload
    Given request requestPayload
    When method POST
    Then status 400
    And assert response != null
    And assert responseStatus == 400
    And match response == {type: 'https://api.conversionmanager.com/errors/bad-request', title: 'Method Argument Not Valid Exception', status: 400, detail: "The request contains an invalid argument. The field to or amount ,object is null, which violates the validation.", instance: '/conversion'}
    Examples:
      | to  |
      | USD |
      | GBP |
      | JPY |