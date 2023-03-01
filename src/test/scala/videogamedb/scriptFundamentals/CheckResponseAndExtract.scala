package videogamedb.scriptFundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseAndExtract extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  val scn = scenario(name = "Check with JSON Path")

    .exec(http(requestName = "Get specific game")
      .get("/videogame/1")
      .check(jsonPath(path = "$.name").is("Resident Evil 4")))
    .pause(1, 10)

    .exec(http("Get all games")
      .get("/videogame")
      .check(jsonPath(path = "$[1].id").saveAs(key = "gameId")))
    .exec { session => println(session); session }

    .exec(http("Get specific game")
      .get("/videogame/#{gameId}")
      .check(jsonPath("$.name").is("Gran Turismo 3"))
      .check(bodyString.saveAs(key = "responseBody")))
    .exec { session => println(session("responseBody")); session }


  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
