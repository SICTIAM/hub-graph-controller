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
package fr.sictiam.hdd.tasks.read

import java.io.ByteArrayOutputStream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.amqp.{IncomingMessage, OutgoingMessage}
import fr.sictiam.amqp.api.AmqpMessage
import fr.sictiam.amqp.api.rpc.AmqpRpcTask
import fr.sictiam.hdd.rdf.RDFClient
import org.apache.jena.query.{ResultSet, ResultSetFormatter}
import play.api.libs.json.{JsNumber, JsString, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-12
  */

class GraphSelectTask(override val topic: String, override val exchangeName: String)(implicit override val system: ActorSystem, override val materializer: ActorMaterializer, override val ec: ExecutionContext) extends AmqpRpcTask {

  override def onMessage(msg: IncomingMessage, params: String*)(implicit ec: ExecutionContext): Future[OutgoingMessage] = {
    logger.debug(s"Message received : \n${Json.prettyPrint(Json.parse(msg.bytes.utf8String))}")
    Json.parse(msg.bytes.utf8String).validate[AmqpMessage].isSuccess match {
      case true => {
        val qry = Json.parse(msg.bytes.utf8String).as[AmqpMessage].body.as[String]
        val future = RDFClient.select(qry)
        future.transform {
          case Success(rs: ResultSet) => Try {
            val head = Map(
              "command" -> JsString(topic),
              "sender" -> JsString(""),
              "timestamp" -> JsNumber(System.currentTimeMillis()),
              "format" -> JsString("JSON"),
              "status" -> JsString("OK")
            )
            val os: ByteArrayOutputStream = new ByteArrayOutputStream()
            ResultSetFormatter.outputAsJSON(os, rs)
            val body = Json.parse(os.toByteArray)
            logger.debug(s"Result:\n${Json.stringify(body)}")
            os.close()
            AmqpMessage(head, body).toOutgoingMessage(msg.properties)
          }
          case Failure(err) => Try {
            val head = Map(
              "command" -> JsString(topic),
              "sender" -> JsString(""),
              "timestamp" -> JsNumber(System.currentTimeMillis()),
              "format" -> JsString("JSON"),
              "status" -> JsString("ERROR")
            )

            val body = Json.obj(
              "errorClass" -> JsString(err.getCause.getClass.getSimpleName),
              "errorMessage" -> JsString(err.getMessage)
            )
            AmqpMessage(head, body).toOutgoingMessage(msg.properties)
          }
        }
      }
      case false => {
        logger.error(s"Unable to parse the message: ${msg.bytes.utf8String}")
        val head = Map(
          "command" -> JsString(topic),
          "sender" -> JsString(""),
          "timestamp" -> JsNumber(System.currentTimeMillis()),
          "format" -> JsString("JSON"),
          "status" -> JsString("ERROR")
        )
        val body = Json.obj(
          "errorClass" -> JsString("MessageParsingException"),
          "errorMessage" -> JsString(s"Unable to parse the message: ${msg.bytes.utf8String}")
        )
        Future(AmqpMessage(head, body).toOutgoingMessage(msg.properties))
      }
    }
  }

}