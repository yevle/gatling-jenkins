package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  val csvFeeder = csv("data/game.csv").circular

  def getGame() = {
    repeat(10) {
      feed(csvFeeder)
        .exec(http("Get Video Game - #{gameName}")
        .get("/videogame/#{gameId}")
        .check(jsonPath("$.name").is("#{gameName}"))
        .check(status.in(200 to 210)))
        .pause(2)
    }
  }

  val scn = scenario("Basic Custom Feeder")
    .exec(getGame())

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
