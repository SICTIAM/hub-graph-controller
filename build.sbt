import Dependencies._
import sbt.url

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "fr.sictiam"
ThisBuild / organizationName := "SICTIAM"

val meta = """META.INF(.)*""".r

lazy val root = (project in file("."))
  .settings(
    name := "hub-graph-controller",
    pomIncludeRepository := { _ => false },
    licenses := Seq("AGPL 3.0" -> url("https://opensource.org/licenses/AGPL-3.0")),
    scmInfo := Some(
      ScmInfo(
        url("https://bitbucket.org/hub-sictiam/hub-graph-controller.git"),
        "scm:git@bitbucket.org:hub-sictiam/hub-graph-controller.git"
      )
    ),
    developers := List(
      Developer(
        id = "ndelaforge",
        name = "Nicolas Delaforge",
        email = "nicolas.delaforge@mnemotix.com",
        url = url("http://www.mnemotix.com")
      ),
      Developer(
        id = "prlherisson",
        name = "Pierre-René Lherisson",
        email = "pr.lherisson@mnemotix.com",
        url = url("http://www.mnemotix.com")
      ),
      Developer(
        id = "mrogelja",
        name = "Mathieu Rogelja",
        email = "mathieu.rogelja@mnemotix.com",
        url = url("http://www.mnemotix.com")
      )
    ),
    //    publishArtifact := true,
    //    publishMavenStyle := true,
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "jitpack" at "https://jitpack.io"
    ),
    test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
      case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
      //        case n if n.startsWith("reference.conf") => MergeStrategy.concat
      case n if n.endsWith(".conf") => MergeStrategy.concat
      case n if n.endsWith(".properties") => MergeStrategy.concat
      case meta(_) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    updateOptions := updateOptions.value.withGigahorse(false),
    mainClass in(Compile, run) := Some("fr.sictiam.hdd.GraphController"),
    mainClass in assembly := Some("fr.sictiam.hdd.GraphController"),
    assemblyJarName in assembly := "hub-graph-controller.jar",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      akkaStreamTestkit % Test,
      logbackClassic,
      amqpLib
    ),
    imageNames in docker := Seq(
      ImageName(
        namespace = Some("sictiam"),
        repository = name.value,
        tag = Some(version.value)
      )
    ),
    buildOptions in docker := BuildOptions(cache = false),
    dockerfile in docker := {
      // The assembly task generates a fat JAR file
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"

      new Dockerfile {
        from("java:jre-alpine")
        add(artifact, artifactTargetPath)
        entryPoint("java", "-jar", artifactTargetPath)
      }
    }
  )
  .enablePlugins(DockerPlugin)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
