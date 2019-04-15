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

import play.api.libs.json.JsValue

import scala.concurrent.ExecutionContext

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-19
  */

object RDFClient {

  private lazy val client: AbstractRDFClient = {
    RDFStoreConfiguration.clientDriver match {
      case "jena" => new JenaRDFClient
      case "rdf4j" => new Rdf4jRDFClient
      case _ => new Rdf4jRDFClient
    }
  }

  def init() = client.init()

  def load(file: String)(implicit ec: ExecutionContext) = client.load(file)

  def create(jsonld: JsValue)(implicit ec: ExecutionContext) = client.create(jsonld)

  def select(qryStr: String)(implicit ec: ExecutionContext) = client.select(qryStr)

  def describe(qryStr: String)(implicit ec: ExecutionContext) = client.describe(qryStr)

  def construct(qryStr: String)(implicit ec: ExecutionContext) = client.construct(qryStr)

  def ask(qryStr: String)(implicit ec: ExecutionContext) = client.ask(qryStr)

  def update(updateStr: String)(implicit ec: ExecutionContext) = client.update(updateStr)

}