package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ARecordedSimulation extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io")
    .inferHtmlResources(AllowList(), DenyList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*\.svg""", """.*detectportal\.firefox\.com.*"""))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("uk-UA,uk;q=0.9,en-US;q=0.8,en;q=0.7")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")


  object Search {
    val searchFeeder = csv("data/search.csv").random
    val search =
      exec(http("Load Home Page")
          .get("/computers"))
        .pause(2)
        .feed(searchFeeder)
        .exec(
          http("Search comp - #{searchCriterion}")
            .get("/computers?f=#{searchCriterion}")
            .check(css("a:contains('#{searchComputerName}')", "href").saveAs("computerURL")))
        .pause(2)
        .exec(
          http("Choose computer - #{searchComputerName}")
            .get("#computerURL"))
        .pause(2)
  }

  object Browse {
    val browse =
      exec(
        http("Load Home Page")
          .get("/computers"))
        .pause(2)
        .exec(
          http("Choose ACE computer")
            .get("/computers?f=ace"))
        .pause(2)
        .exec(
          http("Load Home Page")
            .get("/computers"))
        .pause(2)
        .repeat(5, "count") {
          exec(
            http("Click Next Page - #{count}")
              .get("/computers?p=#{count}")
          ).pause(2)
        }
  }

  object Create {
    val create = exec(
      http("Load HomePage")
        .get("/computers"))
      .pause(2)
      .exec(
        http("Click Create New Computer")
          .get("/computers/new"))
      .pause(2)
      .exec(
        http("Create Computer")
          .post("/computers")
          .formParam("name", "NewTestComp")
          .formParam("introduced", "1995-01-01")
          .formParam("discontinued", "2009-02-02")
          .formParam("company", "1")
      )
      .pause(2)
  }

  object Delete {
    val delete = exec(
      http("Get Computer Details")
        .get("/computers/381"))
      .pause(2)
      .exec(
        http("Click Delete Computer")
          .post("/computers/381/delete")
      )
      .pause(2)
  }

  val admin = scenario("Admin").exec(Search.search, Create.create, Delete.delete)
  val user = scenario("User").exec(Browse.browse, Create.create)

  setUp(admin.inject(atOnceUsers(2)),
    user.inject(
      nothingFor(5),
      atOnceUsers(1),
      rampUsers(2) during(15),
      constantUsersPerSec(5) during(10)
    ))
    .protocols(httpProtocol)
}
