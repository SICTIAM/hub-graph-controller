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
package fr.sictiam.hdd

import java.net.ConnectException

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import fr.sictiam.amqp.api.{AmqpClientConfiguration, AmqpConnectionHandler}
import fr.sictiam.amqp.api.rpc.AmqpRpcController
import fr.sictiam.hdd.exceptions.{RDFClientConnectException, RDFClientUnknownRepositoryException}
import fr.sictiam.hdd.rdf.RDFClient
import fr.sictiam.hdd.tasks.create.{GraphCreateFromJsonLdTask, GraphCreateFromQueryTask}
import fr.sictiam.hdd.tasks.delete.GraphDeleteTask
import fr.sictiam.hdd.tasks.read._
import fr.sictiam.hdd.tasks.update.GraphUpdateTask

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-12
  */
object GraphController extends App with LazyLogging {

  implicit val system = ActorSystem("GraphControllerSystem")
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  def initRdfClient(): Boolean = {
    try {
      logger.info("Initializing connection to RDF store...")
      RDFClient.init()
      logger.info("RDF client initilization [OK].")
      true
    } catch {
      case ex: RDFClientUnknownRepositoryException =>
        logger.error("RDF client initilization [KO]: " + ex.getMessage)
        false
      case ex: RDFClientConnectException =>
        logger.error("RDF client initilization [KO]: " + ex.getMessage)
        false
      case t: Throwable =>
        logger.error("RDF client initilization [KO]: " + t.getMessage)
        false
    }
  }

  def checkAmqpConnection(): Boolean = {
    try {
      val conncheck = new AmqpConnectionHandler{
        override def init: Unit = {}
        override def shutdown: Unit = {}
      }
      val conn = conncheck.connectionProvider.get
      conn.isOpen
    } catch {
      case _: ConnectException => false
      case _: Throwable => false
    }
  }

  val controller = new AmqpRpcController("Graph Controller")

  logger.info("Graph controller starting...")

  RDFClient.mute()

  while (!initRdfClient()) {
    logger.error("Unable to initialize the connection to the triple store. Retrying in 2 seconds...")
    Thread.sleep(2000)
  }

  RDFClient.verbose()

  controller.registerTask("graph.create.triples", new GraphCreateFromJsonLdTask("graph.create.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.insert.triples", new GraphCreateFromQueryTask("graph.insert.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.select", new GraphSelectTask("graph.select", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.construct", new GraphConstructTask("graph.construct", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.ask", new GraphAskTask("graph.ask", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.describe", new GraphDescribeTask("graph.describe", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.update.triples", new GraphUpdateTask("graph.update.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.delete.triples", new GraphDeleteTask("graph.delete.triples", AmqpClientConfiguration.exchangeName))

  while (!checkAmqpConnection()) {
    logger.error("Unable to initialize the connection to AMQP broker. Retrying in 2 seconds...")
    Thread.sleep(2000)
  }

  val starting = controller.start
  logger.info("Graph controller started successfully.")

  starting onComplete {
    case Success(_) => logger.info("Graph controller started successfully.")
    case Failure(err) => {
      err.printStackTrace()
      sys.exit(1)
    }
  }

  sys addShutdownHook {
    Await.result(controller.shutdown, Duration.Inf)
  }
}
