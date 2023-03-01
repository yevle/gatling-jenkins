package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicCustomFeeder extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  var idNumbers = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificGame() = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("Get Video Game with Id - #{gameId}")
          .get("/videogame/#{gameId}")
          .check(status.in(200 to 210)))
        .pause(1)
    }
  }

  val scn = scenario("CSV feeder")
    .exec(getSpecificGame())

  setUp(
    scn.inject(atOnceUsers(1)))
    .protocols(httpProtocol)

}
