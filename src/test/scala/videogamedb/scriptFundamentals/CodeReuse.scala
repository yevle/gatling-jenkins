package videogamedb.scriptFundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CodeReuse extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")

  def getAllGames = {
    exec(http(requestName = "Get all games")
      .get("/videogame")
      .check(status.is(200)))
  }

  def getSpecificGame() = {
    repeat(5,"counter") {
      exec(http(requestName = "Get specific game with id: #{counter}")
        .get("/videogame/#{counter}")
        .check(status.in(200 to 210)))
    }
  }

  def getSpecificGame(num:Int) = {
      exec(http(requestName = s"Get specific game with id: $num")
        .get(s"/videogame/$num")
        .check(status.in(200 to 210)))
  }


  val scn = scenario(name = "Code Reuse")
    .exec(getAllGames)
    .pause(3)
    .exec(getSpecificGame())
    .pause(3)
    .exec(getSpecificGame(5))
    .pause(2)
    .repeat(3){getAllGames}

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
