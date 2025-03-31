Feature: Party API Test with Multiple Requests

  Background:
    * url 'http://localhost:8080'

  Scenario: Creation, fetch and deletion of a party in the service
    Given path '/api/v1/adventurers'
    And def randomAdventurerName1 = 'Adventurer-' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request
      """
			{
				"name": "#(randomAdventurerName1)"
			}
      """
    When method post
    Then status 201
    And match response.id != null
    And match response.name == "#(randomAdventurerName1)"
		* def adventurerId1 = response.id

		Given path '/api/v1/adventurers'
    And def randomAdventurerName2 = 'Adventurer-' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request
      """
			{
				"name": "#(randomAdventurerName2)"
			}
      """
    When method post
    Then status 201
    And match response.id != null
    And match response.name == "#(randomAdventurerName2)"
		* def adventurerId2 = response.id

		Given path '/api/v1/adventurers'
    And def randomAdventurerName3 = 'Adventurer-' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request
      """
			{
				"name": "#(randomAdventurerName3)"
			}
      """
    When method post
    Then status 201
    And match response.id != null
    And match response.name == "#(randomAdventurerName3)"
		* def adventurerId3 = response.id

		Given path '/api/v1/parties'
    And def randomPartyName1 = 'Adventurer-' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request
      """
			{
				"name":  "#(randomPartyName1)",
				"founderId": "#(adventurerId1)",
				"adventurerIds": [#(adventurerId1)]
			}
      """
    When method post
    Then status 201
    And match response.id != null
    And match response.name == "#(randomPartyName1)"
    And match response.founderId == "#(adventurerId1)"
    And match response.adventurerIds == [#(adventurerId1)]
		* def partyId1 = response.id

		Given path '/api/v1/parties'
    And def randomPartyName2 = 'Adventurer-' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request
      """
      {
        "name":  "#(randomPartyName2)",
        "founderId": "#(adventurerId2)",
        "adventurerIds": [#(adventurerId2),#(adventurerId3)]
      }
      """
    When method post
    Then status 201
    And match response.id != null
    And match response.name == "#(randomPartyName2)"
    And match response.founderId == "#(adventurerId2)"
    And match response.adventurerIds == [#(adventurerId2),#(adventurerId3)]
    * def partyId2 = response.id

    Given path '/api/v1/adventurers', adventurerId1
    When method delete
    Then status 400
    * def instance = "/api/v1/adventurers/" + adventurerId1
    And match response == {"type":"about:blank","title":"Bad Request","status":400,"detail":"The founder must be in the adventurer list","instance":#(instance)}

    Given path '/api/v1/parties', partyId1
    When method delete
    Then status 204
    And match response == ""

    Given path '/api/v1/parties', partyId2
    When method delete
    Then status 204
    And match response == ""

    Given path '/api/v1/adventurers', adventurerId1
    When method delete
    Then status 204
    And match response == ""

    Given path '/api/v1/adventurers', adventurerId2
    When method delete
    Then status 204
    And match response == ""

    Given path '/api/v1/adventurers', adventurerId3
    When method delete
    Then status 204
    And match response == ""