/*
 * Copyright 2011-2020 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import scala.concurrent.duration.DurationInt
import scala.concurrent.duration._
import scala.util.Random
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  val y = Iterator.continually(
    Map(
      "randfname" -> (Random.nextInt(99)),
      "randlname" -> (Random.nextInt(99)),
      "randemail" -> (Random.nextInt(99))
    )
  )

  /* ********************************************************************************************************
  ******************************************** Customer *****************************************************
  ******************************************************************************************************** */

  object CreateCustomer {
    val createcustomer = exec(http("Request Name = Create Customer POST")
      .post("http://127.0.0.1:80/api/v1/customer/")
      .header("content-type" , "application/json")
      .body(StringBody(string = """{  "fname": "${randfname}" ,"email": ${randemail},"lname": ${randlname} }"""))
      .check(status.not(404), status.not(500))
      .check(jsonPath("$..customer_id").ofType[String].saveAs("customer_id"))
      )
  }

  object LoginCustomer {
    val logincustomer = exec(http("Request Name = Login Customer PUT")
      .put("http://127.0.0.1:80/api/v1/customer/login")
      .header("content-type" , "application/json")
      .body(StringBody(string = """{   "uid": "${customer_id}" }"""))
      .check(bodyString.saveAs("ResponseTokenLogin"))
      )
  }

  object LogoffCustomer {
    val logoffcustomer = exec(http("Request Name = Logoff Customer PUT")
      .put("http://127.0.0.1:80/api/v1/customer/logoff")
      .header("content-type" , "application/json")
      .body(StringBody(string = """{   "jwt": "${ResponseTokenLogin}" }"""))
      .check(bodyString.saveAs("ResponseTokenLogoff"))
      )
  }

  object UpdateCustomer {
    val updatecustomer = exec(http("Request Name = Update Customer PUT")
      .put("http://127.0.0.1:80/api/v1/customer/${customer_id}")
      .header("content-type" , "application/json")
      .header("authorization", "ResponseTokenLogin")
      .body(StringBody(string = """{  "fname": "updated" ,"email": "updated","lname": "updated" }"""))
      .check(bodyString.saveAs("ResponseTokenUpdateCustomer"))
      )
  }

  object DeleteCustomer {
    val deletecustomer = exec(http("Request Name = Delete Customer DELETE")
      .delete("http://127.0.0.1:80/api/v1/customer/${customer_id}")
      .header("authorization", "ResponseTokenLogin")
      .check(bodyString.saveAs("ResponseTokenDeleteCustomer"))
      )
  }

  object ReadCustomer {
    val readcustomer = exec(http("Request Name = Read Customer GET")
      .get("http://127.0.0.1:80/api/v1/customer/4ab31555-ad0b-495d-bab6-531eca7b4643")
      .header("authorization", "ResponseTokenLogin")
      .check(bodyString.saveAs("ResponseTokenReadCustomer"))
      )
  }

  /* ********************************************************************************************************
  ******************************************** Account  *****************************************************
  ******************************************************************************************************** */

  object CreateAccount {
    val createaccount = exec(http("Request Name = Create Account POST")
      .post("http://127.0.0.1:80/api/v1/account/")
      .header("content-type" , "application/json")
      .header("authorization", "ResponseTokenLogin")
      .body(StringBody(string = """{  "CustomerId": "${customer_id}" ,"AccountType": "Will take random value from csv here.", "Balance": "Will take random value from feeder here." }"""))
      .check(status.not(404), status.not(500))
      .check(jsonPath("$..account_id").ofType[String].saveAs("account_id"))
      )
  }

  object UpdateAccount {
    val updateaccount = exec(http("Request Name = Update Account PUT")
      .put("http://127.0.0.1:80/api/v1/account/${account_id}")
      .header("content-type" , "application/json")
      .header("authorization", "ResponseTokenLogin")
      .body(StringBody(string = """{  "Balance": "Will take UPDATED random value from feeder here." }"""))
      .check(bodyString.saveAs("ResponseTokenUpdateAccount"))
      )
  }

  object DeleteAccount {
    val deleteaccount = exec(http("Request Name = Delete Account DELETE")
      .delete("http://127.0.0.1:80/api/v1/account/${account_id}")
      .header("authorization", "ResponseTokenLogin")
      .check(bodyString.saveAs("ResponseTokenDeleteAccount"))
      )
  }

  object ReadAccount {
    val readaccount = exec(http("Request Name = Read Account GET")
      .get("http://127.0.0.1:80/api/v1/account/${account_id}")
      .header("authorization", "ResponseTokenLogin")
      .check(bodyString.saveAs("ResponseTokenReadAccount"))
      )
  }

  /* ********************************************************************************************************
  ******************************************* Transaction ***************************************************
  ******************************************************************************************************** */

  object CreateTransaction {
    val createtransaction = exec(http("Request Name = Create Transaction POST")
      .post("http://127.0.0.1:80/api/v1/transaction/")
      .header("content-type" , "application/json")
      .header("authorization", "ResponseTokenLogin")
      .body(StringBody(string = """{  "AccountId": "${account_id}" ,"TransactionType": "Will take random value from csv here.", "Amount": "Will take random value from feeder here." }"""))
      .check(status.not(404), status.not(500))
      .check(jsonPath("$..transaction_id").ofType[String].saveAs("transaction_id"))
      )
  }

  object DeleteTransaction {
    val deletetransaction = exec(http("Request Name = Delete Transaction DELETE")
      .delete("http://127.0.0.1:80/api/v1/transaction/${transaction_id}")
      .header("authorization", "ResponseTokenLogin")
      .check(bodyString.saveAs("ResponseTokenDeleteTransaction"))
      )
  }

  object ReadTransaction {
    val readtransaction = exec(http("Request Name = Read Transaction GET")
      .get("http://127.0.0.1:80/api/v1/transaction/${transaction_id}")
      .header("authorization", "ResponseTokenLogin")
      .check(bodyString.saveAs("ResponseTokenReadTransaction"))
      )
  }

  /* ********************************************************************************************************
  ******************************************** Scenarios ****************************************************
  ******************************************************************************************************** */

  // This Scenario will create a customer and save its customer_id in the session
  val Createcustomers = scenario("Your Scenario Name is => Create customer (with Random values feeder)")
              .feed(y)
              .exec(Createcustomer.createcustomer)
              .exec { session =>
                      println(session+"\n"+session("customer_id").as[String]+"\n\n")
                      session
                    }
  setUp(Createcustomers.inject(atOncecustomers(1)))
  
}
