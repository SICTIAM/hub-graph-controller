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
package fr.sictiam.hdd.exceptions

/**
  * Created by Nicolas DELAFORGE (nicolas.delaforge@mnemotix.com).
  * Date: 2019-03-19
  */

abstract class RDFClientException(message: String, cause: Throwable) extends Exception(message, cause)

case class RDFClientConnectException(message: String, cause: Throwable) extends RDFClientException(message, cause)

case class RDFClientUnknownRepositoryException(message: String, cause: Throwable) extends RDFClientException(message, cause)

case class RDFLoadException(message: String, cause: Throwable) extends RDFClientException(message, cause)

case class RDFUpdateException(message: String, cause: Throwable) extends RDFClientException(message, cause)

case class RDFConstructException(message: String, cause: Throwable) extends RDFClientException(message, cause)

case class RDFAskException(message: String, cause: Throwable) extends RDFClientException(message, cause)

case class RDFSelectException(message: String, cause: Throwable) extends RDFClientException(message, cause)