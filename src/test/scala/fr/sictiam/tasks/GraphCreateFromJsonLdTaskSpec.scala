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
package fr.sictiam.tasks

import fr.sictiam.AmqpSpec
import fr.sictiam.amqp.api.{AmqpClientConfiguration, AmqpMessage}
import fr.sictiam.hdd.rdf.RDFClient
import fr.sictiam.hdd.tasks.create.GraphCreateFromJsonLdTask
import play.api.libs.json.Json

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-19
  */

class GraphCreateFromJsonLdTaskSpec extends AmqpSpec {
  "GraphCreateFromJsonLdTask" should {
    val task = new GraphCreateFromJsonLdTask("graph.create.triples", AmqpClientConfiguration.exchangeName)
    "create triples from an amqp message containing a JSON-LD body" in {
      val jsonld =
        """
          |{
          |  "@context": {
          |    "dc": "http://purl.org/dc/elements/1.1/",
          |    "ex": "http://example.org/vocab#",
          |    "xsd": "http://www.w3.org/2001/XMLSchema#",
          |    "ex:contains": {
          |      "@type": "@id"
          |    }
          |  },
          |  "@graph": [
          |    {
          |      "@id": "http://example.org/library",
          |      "@type": "ex:Library",
          |      "ex:contains": "http://example.org/library/the-republic"
          |    },
          |    {
          |      "@id": "http://example.org/library/the-republic",
          |      "@type": "ex:Book",
          |      "dc:creator": "Plato",
          |      "dc:title": "The Republic",
          |      "ex:contains": "http://example.org/library/the-republic#introduction"
          |    },
          |    {
          |      "@id": "http://example.org/library/the-republic#introduction",
          |      "@type": "ex:Chapter",
          |      "dc:description": "An introductory chapter on The Republic.",
          |      "dc:title": "The Introduction"
          |    }
          |  ]
          |}
        """.stripMargin
      val message = AmqpMessage(Map.empty, Json.parse(jsonld))
      Await.result(task.onMessage(message.toIncomingMessage()), Duration.Inf)

      RDFClient.select("SELECT * WHERE { <http://example.org/library/the-republic> ?r ?q }")
    }
  }
}
