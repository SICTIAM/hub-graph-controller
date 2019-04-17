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
package fr.sictiam.hdd.rdf

import com.typesafe.scalalogging.LazyLogging
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-19
  */
trait AbstractRDFClient extends LazyLogging {

  var isVerbose:Boolean=true

  def verbose() = isVerbose=true

  def mute() = isVerbose=false

  def init()(implicit ec: ExecutionContext): Unit

  def load(file: String)(implicit ec: ExecutionContext): Future[Unit]

  def create(jsonld: JsValue)(implicit ec: ExecutionContext): Future[Unit]

  def select(qryStr: String)(implicit ec: ExecutionContext): Future[ResultSet]

  def describe(qryStr: String)(implicit ec: ExecutionContext): Future[Model]

  def construct(qryStr: String)(implicit ec: ExecutionContext): Future[Model]

  def ask(qryStr: String)(implicit ec: ExecutionContext): Future[Boolean]

  def update(updateStr: String)(implicit ec: ExecutionContext): Future[Unit]

}
