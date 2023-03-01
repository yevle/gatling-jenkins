package demostore


import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random

class RecordedDemostore extends Simulation {

  val DOMAIN = "demostore.gatling.io"

  private val httpProtocol = http
    .baseUrl(s"http://${DOMAIN}")

  val categoryFeeder = csv("data/category.csv").random

  val jsonFeeder = jsonFile("data/jsonProducts.json").random

  val loginFeeder = csv("data/loginDetails.csv").circular

  val rnd = new Random()

  def rndString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val initSession = exec(flushCookieJar)
    .exec(session => session.set("randomNumber", rnd.nextInt()))
    .exec(session => session.set("customerLoggedIn", false))
    .exec(session => session.set("cartTotal", 0.00))
    .exec(addCookie(Cookie("sessionId", rndString(20)).withDomain(DOMAIN)))
//    .exec { session => println(session); session }

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


  object Catalog {
    object Category {
      def view = {
        feed(categoryFeeder)
          .exec(http("Load Category #{categoryName}")
            .get("/category/#{categorySlug}")
            .check(css("#CategoryName").is("#{categoryName}")))
      }
    }

    object Product {
      def view = {
        feed(jsonFeeder)
          .exec(http("Load Product Page - #{name}")
            .get("/product/#{slug}")
            .check(css("#ProductDescription").is("#{description}"))
          )
      }

      def add = {
        exec(view)
          .exec(http("Add product to cart")
            .get("/cart/add/#{id}")
            .check(substring("items in your cart"))
          )
          .exec(session => {
            val currentTotal = session("cartTotal").as[Double]
            val itemPrice = session("price").as[Double]
            session.set("cartTotal", (currentTotal + itemPrice))
          })
      }
    }
  }

  object Checkout {
    def viewCart = {
      doIf(session => !session("customerLoggedIn").as[Boolean]) {
        exec(Customer.login)
      }
        .exec(http("View Cart")
          .get("/cart/view")
          .check(css("#grandTotal").is("$#{cartTotal}"))
        )
    }

    def completeCheckout = {
      exec(
        http("Checkout")
          .get("/cart/checkout")
          .check(substring("Thanks for your order! See you soon!"))
      )
    }
  }

  object Customer {
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

  private val scn = scenario("demostore.RecordedDemostore")
    .exec(initSession)
    .exec(CmsPages.homePage)
    .pause(1)
    .exec(CmsPages.aboutUsPage)
    .pause(1)
    .exec(Catalog.Category.view)
    .pause(1)
    .exec(Catalog.Product.add)
    .pause(1)
    .exec(Checkout.viewCart)
    .pause(1)
    .exec(Catalog.Product.add)
    .pause(1)
    .exec(Checkout.viewCart)
    .pause(1)
    .exec(Checkout.completeCheckout)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
