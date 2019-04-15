/**
  * Copyright (C) 2013-2018 MNEMOTIX <http://www.mnemotix.com/> and/or its affiliates
  * and other contributors as indicated by the @author tags.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package fr.sictiam.tasks

import fr.sictiam.AmqpSpec
import fr.sictiam.amqp.api.{AmqpClientConfiguration, AmqpMessage}
import fr.sictiam.hdd.tasks.read.GraphAskTask
import play.api.libs.json.{JsBoolean, JsString, Json}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, _}

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-04-15
  */

class GraphAskTaskSpec extends AmqpSpec {

  override implicit val patienceConfig = PatienceConfig(10.seconds)

  "GraphAskTask" should {
    val task = new GraphAskTask("graph.ask", AmqpClientConfiguration.exchangeName)
    "execute an ask query" in {
      val qryStr = "ASK WHERE {?p ?r ?q} LIMIT 1"
      val message = AmqpMessage(Map.empty, JsString(qryStr))
      val resultMessage = Await.result(task.onMessage(message.toIncomingMessage()), Duration.Inf)
      val msg = Json.parse(resultMessage.bytes.utf8String).as[AmqpMessage]
      msg.body shouldBe JsBoolean(true)
    }
    "send an error message on a malformed query" in {
      val qryStr = "AS WHERE {?p ?r ?q} LIMIT 1"
      val message = AmqpMessage(Map.empty, JsString(qryStr))
      val resultMessage = Await.result(task.onMessage(message.toIncomingMessage()), Duration.Inf)
      println(resultMessage.bytes.utf8String)
      val msg = Json.parse(resultMessage.bytes.utf8String).as[AmqpMessage]
      msg.headers("status") shouldEqual JsString("ERROR")
    }
  }
}
