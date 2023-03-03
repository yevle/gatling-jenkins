package demostore.pageObjects

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object CmsPages {

  def homePage = {
    exec(
      http("Load Home Page")
        .get("/")
        .check(regex("<title>Gatling Demo-Store</title>").exists)
        .check(css("#_csrf", "content").saveAs("csrfValue"))
    )
  }

  def aboutUsPage = {
    exec(
      http("Load About Us")
        .get("/about-us")
        .check(substring("About Us"))
    )
  }

}
