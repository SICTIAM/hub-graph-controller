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

import fr.sictiam.hdd.exceptions._
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdfconnection.RDFConnectionFactory
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-19
  */
class Rdf4jRDFClient extends AbstractRDFClient {


  override def init(): Unit = ???

  override def load(file: String)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def create(jsonld: JsValue)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def select(qryStr: String)(implicit ec: ExecutionContext): Future[ResultSet] = ???

  override def describe(qryStr: String)(implicit ec: ExecutionContext): Future[Model] = ???

  override def construct(qryStr: String)(implicit ec: ExecutionContext): Future[Model] = ???

  override def ask(qryStr: String)(implicit ec: ExecutionContext): Future[Boolean] = ???

  override def update(updateStr: String)(implicit ec: ExecutionContext): Future[Unit] = ???

  private def getReadConnection() = {
    try {
      val suffix = if (RDFStoreConfiguration.readEndpoint.length == 0) "" else s"/${RDFStoreConfiguration.readEndpoint}"
      RDFConnectionFactory.connectPW(s"${RDFStoreConfiguration.rootUri}/${RDFStoreConfiguration.repositoryName}$suffix", RDFStoreConfiguration.user, RDFStoreConfiguration.pwd)
    } catch {
      case t: Throwable => throw new RDFClientConnectException("Unable to connect to the SPARQL Read endpoint", t)
    }
  }

  private def getWriteConnection() = {
    try {
      RDFConnectionFactory.connectPW(s"${RDFStoreConfiguration.rootUri}/${RDFStoreConfiguration.repositoryName}/${RDFStoreConfiguration.writeEndpoint}", RDFStoreConfiguration.user, RDFStoreConfiguration.pwd)
    } catch {
      case t: Throwable => throw new RDFClientConnectException("Unable to connect to the SPARQL Write endpoint", t)
    }
  }

}
