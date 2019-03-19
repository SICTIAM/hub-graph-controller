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
import fr.sictiam.hdd.tasks._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-12
  */
object GraphController extends App {

  implicit val system = ActorSystem("GraphControllerSystem")
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val controller = new AmqpRpcController("Graph Controller")
  controller.registerTask("graph.create.triples", new GraphCreateFromJsonLdTask("graph.create.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.read.triples", new GraphReadTask("graph.read.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.update.triples", new GraphUpdateTask("graph.update.triples", AmqpClientConfiguration.exchangeName))
  controller.registerTask("graph.delete.triples", new GraphDeleteTask("graph.delete.triples", AmqpClientConfiguration.exchangeName))

  controller.start

  sys addShutdownHook {
    Await.result(controller.shutdown, Duration.Inf)
  }
}
