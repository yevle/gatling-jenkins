package videogamedb.scriptFundamentals;

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class Authenticate extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  def authenticate() = {
    exec(http("Authenticate")
      .post("/authenticate")
      .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
      .check(jsonPath(path = "$.token").saveAs(key = "token"))
    )
  }

  def createGame() = {
    exec(http("Create New Game")
      .post("/videogame")
      .header("Authorization", "Bearer #{token}")
      .body(StringBody("{\n  \"category.csv\": \"Platform\",\n  \"name\": \"Mario\",\n  \"rating\": \"Mature\",\n  \"releaseDate\": \"2012-05-04\",\n  \"reviewScore\": 85\n}"))
    )
  }

  val scn = scenario("Autenticate")
    .exec(authenticate())
    .exec(createGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
