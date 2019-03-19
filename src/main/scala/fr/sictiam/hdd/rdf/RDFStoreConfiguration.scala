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

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-19
  */

object RDFStoreConfiguration {
  lazy val conf: Config = Option(ConfigFactory.load().getConfig("rdfstore")).getOrElse(ConfigFactory.empty())
  lazy val user: String = Option(conf.getString("user")).getOrElse("admin")
  lazy val pwd: String = Option(conf.getString("password")).getOrElse("Pa55w0rd")
  lazy val rootUri: String = Option(conf.getString("root.uri")).getOrElse("localhost")
  lazy val repositoryName: String = Option(conf.getString("repository.name")).getOrElse("hdd")
  lazy val readEndpoint: String = Option(conf.getString("read.endpoint")).getOrElse("sparql")
  lazy val writeEndpoint: String = Option(conf.getString("write.endpoint")).getOrElse("update")
}
