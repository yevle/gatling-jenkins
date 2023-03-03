package demostore.pageObjects

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Customer {

  val loginFeeder = csv("data/loginDetails.csv").circular

  def login = {
    feed(loginFeeder)
      .exec(http("Load Login Page")
        .get("/login")
        .check(substring("Username:")))
      .exec(
        http("Login User - #{username}")
          .post("/login")
          .formParam("_csrf", "#{csrfValue}")
          .formParam("username", "#{username}")
          .formParam("password", "#{password}")
      )
      .exec(session => session.set("customerLoggedIn", true))
  }
}
