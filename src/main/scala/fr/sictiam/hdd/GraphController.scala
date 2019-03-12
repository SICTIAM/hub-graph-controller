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

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fr.sictiam.amqp.api.controllers.AmqpController
import fr.sictiam.hdd.tasks.{GraphCreateTask, GraphDeleteTask, GraphReadTask, GraphUpdateTask}

import scala.concurrent.ExecutionContext

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-12
  */
object GraphController extends App {

  implicit val system = ActorSystem("GraphControllerSystem")
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(10)

    def execute(runnable: Runnable) {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable) {}
  }

  val controller = new AmqpController("hddexchange", "Graph Controller")
  controller.registerTask("graph.create", new GraphCreateTask)
  controller.registerTask("graph.read", new GraphReadTask)
  controller.registerTask("graph.update", new GraphUpdateTask)
  controller.registerTask("graph.delete", new GraphDeleteTask)

  controller.start

  sys addShutdownHook {
    controller.shutdown
  }
}
