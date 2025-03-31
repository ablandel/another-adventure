Feature: Adventurer API Test with Multiple Requests

  Background:
    * url 'http://localhost:8080'

  Scenario: Creation, fetch and deletion of an adventurer in the service
    Given path '/api/v1/adventurers'
    And def randomName = 'Adventurer-' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request
      """
			{
				"name": "#(randomName)"
			}
      """
    When method post
    Then status 201
    And match response.id != null
    And match response.name == "#(randomName)"
		* def id = response.id

    Given path '/api/v1/adventurers', id
    When method get
    Then status 200
    And match response == {"id":#(id),"name":"#(randomName)"}

    Given path '/api/v1/adventurers', id
    When method delete
    Then status 204
    And match response == ""

    Given path '/api/v1/adventurers', id
    When method get
    Then status 400
    * def expectedDetail = "The adventurer with ID `" + id + "` does not exist"
    * def instance = "/api/v1/adventurers/" + id
    And match response == {"type":"about:blank","title":"Bad Request","status":400,"detail":#(expectedDetail),"instance":#(instance)}
