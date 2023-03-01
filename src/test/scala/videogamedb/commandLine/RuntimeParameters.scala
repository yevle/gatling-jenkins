package videogamedb.commandLine

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RuntimeParameters extends Simulation {

  // 1 Http Configuration

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  def USERCOUNT = System.getProperty("USERS", "5").toInt

  def RAMPDURATION = System.getProperty("RAMP_DURATION", "10").toInt

  def TESTDURATION = System.getProperty("TEST_DURATION", "30").toInt

  before {
    println(s"Users counr ${USERCOUNT} with ramp duration ${RAMPDURATION} and testduration ${TESTDURATION}")
  }

  // 2 Scenario Definition
  def getAllVideoGames() = {
    exec(http(requestName = "Get all games")
      .get("/videogame"))
  }

  def getSpecificGame() = {
    exec(http(requestName = "Get specific game")
      .get("/videogame/2"))
  }

  val scn = scenario(name = "Run fromm CL")
    .forever {
      exec(getAllVideoGames())
        .pause(1)
        .exec(getSpecificGame())
        .pause(1)
    }

  // 3 Load Scenario

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(USERCOUNT).during(RAMPDURATION)
    ).protocols(httpProtocol)
  ).maxDuration(TESTDURATION)

}
