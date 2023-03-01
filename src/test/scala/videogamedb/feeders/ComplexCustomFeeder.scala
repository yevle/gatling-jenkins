package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

class ComplexCustomFeeder extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk/api")
    .acceptHeader(value = "application/json")
    .contentTypeHeader(value = "application/json")

  var idNumbers = (1 to 10).iterator

  val random = new Random()

  def rndString(length: Int) = random.alphanumeric.filter(_.isLetter).take(length).mkString

  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def getRandomDate(days: Int) = LocalDate.now().minusDays(days).format(pattern)

  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game - " + rndString(6)),
    "releaseDate" -> getRandomDate(random.nextInt(1000)),
    "reviewScore" -> random.nextInt(100),
    "category.csv" -> ("Category - " + rndString(4)),
    "rating" -> ("Rating - " + rndString(4))
  )
  )

  def authenticate() = {
    exec(http("Authenticate")
      .post("/authenticate")
      .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
      .check(jsonPath(path = "$.token").saveAs(key = "token"))
    )
  }

  def createGame() = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("Create New Game - #{name} with #{gameId}")
          .post("/videogame")
          .header("Authorization", "Bearer #{token}")
          .body(ElFileBody("bodies/gameTemplate.json")).asJson
          .check(bodyString.saveAs("responseBody")))
        .exec { session => println(session("responseBody").as[String]); session }
        .pause(1)
    }
  }

  val scn = scenario("Complex Custom Feeder")
    .exec(authenticate())
    .exec(createGame())

  setUp(
    scn.inject(atOnceUsers(1)))
    .protocols(httpProtocol)

}
