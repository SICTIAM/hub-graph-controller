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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fr.sictiam.amqp.api.AmqpClientConfiguration
import fr.sictiam.amqp.api.rpc.AmqpRpcController
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
object GraphController extends App {

  implicit val system = ActorSystem("GraphControllerSystem")
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val controller = new AmqpRpcController("Graph Controller")

  RDFClient.init()

  controller.registerTask("graph.create.triples", new GraphCreateFromJsonLdTask("graph.create.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.insert.triples", new GraphCreateFromQueryTask("graph.insert.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.select", new GraphSelectTask("graph.select", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.construct", new GraphConstructTask("graph.construct", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.ask", new GraphAskTask("graph.ask", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.describe", new GraphDescribeTask("graph.describe", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.update.triples", new GraphUpdateTask("graph.update.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.delete.triples", new GraphDeleteTask("graph.delete.triples", AmqpClientConfiguration.exchangeName))

  val starting = controller.start
  starting onComplete {
    case Success(_) =>
    case Failure(_) => sys.exit(1)
  }


  sys addShutdownHook {
    Await.result(controller.shutdown, Duration.Inf)
  }
}
