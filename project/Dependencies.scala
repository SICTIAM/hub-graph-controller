import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val jodaTime = "joda-time" % "joda-time" % "2.10.1"
  // lazy val guava = "com.google.guava" % "guava" % "27.0.1-jre"
  lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.3"

  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.5.19"
  lazy val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.19"
  lazy val alpakkaAmqp = "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "1.0-M2"

  lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % "2.5.19"
  lazy val playJson = "com.typesafe.play" %% "play-json" % "2.7.0"
  lazy val playWs = "com.typesafe.play" %% "play-ws" % "2.7.0-RC9"
  lazy val playWSAhc = "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.0-RC2"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  lazy val rabbitmqClient = "com.rabbitmq" % "amqp-client" % "5.6.0"
  lazy val jenaLibs = "org.apache.jena" % "apache-jena-libs" % "3.10.0" pomOnly()
  lazy val amqpLib = "fr.sictiam" %% "hub-amqp-lib" % "0.1.2-SNAPSHOT"
  lazy val rdf4jRuntime = "org.eclipse.rdf4j" % "rdf4j-runtime" % "2.5.0"

}
