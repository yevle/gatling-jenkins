package demostore.pageObjects

import io.gatling.core.Predef._
import io.gatling.http.Predef._

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
