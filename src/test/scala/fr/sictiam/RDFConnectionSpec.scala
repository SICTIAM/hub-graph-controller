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
package fr.sictiam

import akka.dispatch.ExecutionContexts
import fr.sictiam.hdd.rdf.RDFClient
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-18
  */

class RDFConnectionSpec extends WordSpec with Matchers with BeforeAndAfterAll with ScalaFutures {

  override implicit val patienceConfig = PatienceConfig(10.seconds)

  "RDFConnection" should {
    implicit lazy val ec: ExecutionContext = ExecutionContexts.global()
    "open connection to the triple store, load data into it" in {
      val filepath = this.getClass.getResource("/data.ttl").toURI.getPath
      RDFClient.load(filepath).futureValue
    }
    "query data with SPARQL select queries" in {
      RDFClient.select("SELECT * WHERE {?p ?r ?q} LIMIT 10").map { rs =>
        while (rs.hasNext) {
          val qs = rs.next()
          println(s"${qs.getResource("?p").getURI} -- ${qs.getResource("?r").getURI} --> ${qs.getResource("?q").getURI}")
        }
      }.futureValue
    }
    "insert data with SPARQL Update query" in {
      val qry =
        """PREFIX dc: <http://purl.org/dc/elements/1.1/>
          |INSERT DATA
          |{ <http://example/book1> dc:title "A new book" ; dc:creator "A.N.Other" .}
          |""".stripMargin

      RDFClient.update(qry).futureValue
    }
    "delete data with SPARQL Update query" in {
      val qry =
        """PREFIX dc: <http://purl.org/dc/elements/1.1/>
          |
          |DELETE DATA
          |{
          |  <http://example/book2> dc:title "David Copperfield" ;
          |                         dc:creator "Edmund Wells" .
          |}
          |""".stripMargin

      val resultSet = RDFClient.update(qry).futureValue
    }
  }
}
