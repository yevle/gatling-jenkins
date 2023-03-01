package videogamedb.scriptFundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseCode extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  val scn = scenario(name = "Video Game DB - 3 Calls")

    .exec(http(requestName = "Get all games - 1st call")
      .get("/videogame")
      .check(status.is(200)))
    .pause(duration = 5)


    .exec(http(requestName = "Get specific game")
      .get("/videogame/1")
      .check(status.in(200 to 210)))
    .pause(1, 10)

    .exec(http(requestName = "Get all games - 2nd call")
      .get("/videogame")
      .check(status.not(404), status.not(505)))
    .pause(3)


  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)


}
