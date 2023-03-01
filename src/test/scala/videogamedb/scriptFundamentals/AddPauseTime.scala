package videogamedb.scriptFundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.recorder.internal.bouncycastle.oer.its.ieee1609dot2.basetypes.Duration


class AddPauseTime extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  val scn = scenario(name = "Video Game DB - 3 Calls")

    .exec(http(requestName = "Get all games - 1st call")
      .get("/videogame"))
    .pause(duration = 5)

    .exec(http(requestName = "Get specific game")
      .get("/videogame/1"))
    .pause(1, 10)

    .exec(http(requestName = "Get all games - 2nd call")
      .get("/videogame"))
    .pause(3000, Duration.milliseconds)

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
