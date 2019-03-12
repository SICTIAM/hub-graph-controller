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
package fr.sictiam.hdd.tasks

import fr.sictiam.amqp.api.controllers.AmqpTask
import play.api.libs.json.{JsNumber, JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-12
  */

class GraphCreateTask extends AmqpTask {
  override def process(parameter: JsValue)(implicit ec: ExecutionContext): Future[JsValue] = {
    println("create task/" + Json.stringify(parameter))
    Future(JsNumber(Json.stringify(parameter).length))(ec)
  }
}
