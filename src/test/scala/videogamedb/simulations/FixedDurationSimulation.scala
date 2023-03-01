package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FixedDurationSimulation extends Simulation {

  // 1 Http Configuration

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  def getAllVideoGames() = {
    exec(http(requestName = "Get all games")
      .get("/videogame"))
  }

  def getSpecificGame() = {
    exec(http(requestName = "Get specific game")
      .get("/videogame/2"))
  }

  // 2 Scenario Definition

  val scn = scenario(name = "Fixed Duration Sim")
    .forever {
      exec(getAllVideoGames())
        .pause(3)
        .exec(getSpecificGame())
        .pause(3)
        .exec(getAllVideoGames())
    }

  // 3 Load Scenario

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(5),
      rampUsers(10).during(20)
    ).protocols(httpProtocol)
  ).maxDuration(50)

}
