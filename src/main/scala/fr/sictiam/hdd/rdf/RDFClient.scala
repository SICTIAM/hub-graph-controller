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

import java.io.StringReader

import com.typesafe.scalalogging.LazyLogging
import fr.sictiam.hdd.exceptions._
import org.apache.jena.rdf.model.{Model, ModelFactory, StmtIterator}
import org.apache.jena.rdfconnection.RDFConnectionFactory
import org.apache.jena.riot.RiotException
import org.apache.jena.system.Txn
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-19
  */
object RDFClient extends LazyLogging {

  def load(file: String)(implicit ec: ExecutionContext) = {
    val future = Future {
      val conn = getWriteConnection()
      Txn.executeWrite(conn, () => {
        conn.load(file)
      })
      conn.close()
    }
    future onComplete {
      case Success(_) => logger.info("The dataset was loaded successfully.")
      case Failure(err) => {
        logger.error("The store failed to load the dataset.", err)
        throw new RDFLoadException("The store failed to load the dataset.", err)
      }
    }
    future
  }

  def create(jsonld: JsValue)(implicit ec: ExecutionContext) = {
    val future = Future {
      val reader = new StringReader(Json.stringify(jsonld))
      val m: Model = ModelFactory.createDefaultModel
      try {
        m.read(reader, null, "JSON-LD")
        val conn = getWriteConnection()
        Txn.executeWrite(conn, () => {
          conn.load(m)
        })
        conn.close()
      } catch {
        case e: RiotException => throw new MessageParsingException("Unable to parse the message body. Well formed JSON-LD string is expected.", e)
      }
      finally if (reader != null) reader.close()
    }
    //        RDFParser.create.source(sr).lang(RDFLanguages.JSONLD).errorHandler(ErrorHandlerFactory.errorHandlerStrict).base(RDFParserConfiguration.baseUri).parse(model)
    future onComplete {
      case Success(_) => logger.info("The data was loaded successfully.")
      case Failure(err) => {
        logger.error("The store failed to load the data.", err)
        throw new RDFLoadException("The store failed to load the data.", err)
      }
    }
    future
  }

  def select(qryStr: String)(implicit ec: ExecutionContext) = {
    val future = Future {
      val conn = getReadConnection()
      val rs = conn.query(qryStr).execSelect()
      conn.close()
      rs
    }
    future onComplete {
      case Success(_) => logger.info("The dataset was loaded successfully.")
      case Failure(err) => {
        logger.error("The RDF client failed to execute the query.", err)
        throw new RDFSelectException("The RDF client failed to execute the query.", err)
      }
    }
    future
  }

  def describe(qryStr: String)(implicit ec: ExecutionContext) = {
    val future = Future {
      val conn = getReadConnection()
      val model = conn.queryDescribe(qryStr)
      conn.close()
      model
    }

    future onComplete {
      case Success(_) => logger.info("The dataset was loaded successfully.")
      case Failure(err) => {
        logger.error("The RDF client failed to execute the query.", err)
        throw new RDFConstructException("The RDF client failed to execute the query.", err)
      }
    }
    future
  }

  def construct(qryStr: String)(implicit ec: ExecutionContext) = {
    val future = Future {
      val conn = getReadConnection()
      val model = conn.queryConstruct(qryStr)
      conn.close()
      model
    }

    future onComplete {
      case Success(_) => logger.info("The dataset was loaded successfully.")
      case Failure(err) => {
        logger.error("The RDF client failed to execute the query.", err)
        throw new RDFConstructException("The RDF client failed to execute the query.", err)
      }
    }
    future
  }

  def ask(qryStr: String)(implicit ec: ExecutionContext) = {
    val future = Future {
      val conn = getReadConnection()
      val answer = conn.queryAsk(qryStr)
      conn.close()
      answer
    }
    future onComplete {
      case Success(_) => logger.info("The dataset was loaded successfully.")
      case Failure(err) => {
        logger.error("The RDF client failed to execute the query.", err)
        throw new RDFAskException("The RDF client failed to execute the query.", err)
      }
    }
    future
  }

  def update(jsonld: JsValue)(implicit ec: ExecutionContext) = {
    val future = Future {
      val reader = new StringReader(Json.stringify(jsonld))
      val m: Model = ModelFactory.createDefaultModel
      try {
        m.read(reader, null, "JSON-LD")
        val conn = getWriteConnection()
        val it: StmtIterator = m.listStatements
        while (it.hasNext) {
          val stmt = it.next
          // do your stuff with the Statement (which is a triple)
        }
        //        Txn.executeWrite(conn, () => {
        //          conn.load(m)
        //        })
        conn.close()
      } catch {
        case e: RiotException => throw new MessageParsingException("Unable to parse the message body. Well formed JSON-LD string is expected.", e)
      }
      finally if (reader != null) reader.close()
    }
    //        RDFParser.create.source(sr).lang(RDFLanguages.JSONLD).errorHandler(ErrorHandlerFactory.errorHandlerStrict).base(RDFParserConfiguration.baseUri).parse(model)
    future onComplete {
      case Success(_) => logger.info("The data was loaded successfully.")
      case Failure(err) => {
        logger.error("The store failed to load the data.", err)
        throw new RDFLoadException("The store failed to load the data.", err)
      }
    }
    future
  }

  def update(updateStr: String)(implicit ec: ExecutionContext) = {
    val future = Future {
      val conn = getWriteConnection()
      conn.update(updateStr)
      conn.close()
    }
    future onComplete {
      case Success(_) => logger.info("The dataset was loaded successfully.")
      case Failure(err) => {
        logger.error("The RDF client failed to update the dataset.", err)
        throw new RDFUpdateException("The RDF client failed to update the dataset.", err)
      }
    }
    future
  }

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
