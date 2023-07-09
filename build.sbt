ThisBuild / organization := "es.eriktorr"
ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "3.3.0"

ThisBuild / scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-source:future", // https://github.com/oleg-py/better-monadic-for
  "-Yexplicit-nulls", // https://docs.scala-lang.org/scala3/reference/other-new-features/explicit-nulls.html
  "-Ysafe-init", // https://docs.scala-lang.org/scala3/reference/other-new-features/safe-initialization.html
  "-Wunused:all",
)

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / semanticdbEnabled := true

addCommandAlias(
  "check",
  "; undeclaredCompileDependenciesTest; unusedCompileDependenciesTest; scalafixAll; scalafmtSbtCheck; scalafmtCheckAll",
)

lazy val commons = project
  .in(file("libs/commons"))
  .configure(baseSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.comcast" %% "ip4s-core" % "3.3.0" % Optional,
      "com.monovore" %% "decline" % "2.4.1" % Optional,
      "com.zaxxer" % "HikariCP" % "5.0.1" % Optional exclude ("org.slf4j", "slf4j-api"),
      "io.github.iltotore" %% "iron" % "2.2.0-RC1" % Optional,
      "io.github.iltotore" %% "iron-cats" % "2.2.0-RC1" % Optional,
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC4" % Optional,
      "org.tpolecat" %% "doobie-free" % "1.0.0-RC4" % Optional,
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4" % Optional,
      "org.typelevel" %% "cats-collections-core" % "0.9.7" % Optional,
      "org.typelevel" %% "cats-core" % "2.9.0" % Optional,
      "org.typelevel" %% "cats-effect" % "3.5.0" % Optional,
      "org.typelevel" %% "cats-effect-kernel" % "3.5.0" % Optional,
      "org.typelevel" %% "cats-kernel" % "2.9.0" % Optional,
    ),
  )
  .disablePlugins(ScalafixPlugin) // disabling until all dependencies support Scala 3.3.x

lazy val `fleet-control` = project
  .in(file("apps/fleet-control"))
  .configure(baseSettings)
  .settings(
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "3.7.0",
      "co.fs2" %% "fs2-io" % "3.7.0",
      "com.comcast" %% "ip4s-core" % "3.3.0",
      "com.monovore" %% "decline" % "2.4.1",
      "com.monovore" %% "decline-effect" % "2.4.1",
      "io.circe" %% "circe-core" % "0.14.5",
      "io.github.arainko" %% "ducktape" % "0.1.9",
      "io.github.iltotore" %% "iron" % "2.2.0-RC1",
      "io.github.iltotore" %% "iron-cats" % "2.2.0-RC1",
      "com.lmax" % "disruptor" % "3.4.4" % Runtime,
      "com.mysql" % "mysql-connector-j" % "8.0.33" % Runtime,
      "com.zaxxer" % "HikariCP" % "5.0.1" exclude ("org.slf4j", "slf4j-api"),
      "org.http4s" %% "http4s-circe" % "0.23.22",
      "org.http4s" %% "http4s-core" % "0.23.22",
      "org.http4s" %% "http4s-dsl" % "0.23.22",
      "org.http4s" %% "http4s-ember-server" % "0.23.22",
      "org.http4s" %% "http4s-server" % "0.23.22",
      "org.apache.logging.log4j" % "log4j-core" % "2.20.0" % Runtime,
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.20.0" % Runtime,
      "org.typelevel" %% "case-insensitive" % "1.4.0",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4",
      "org.typelevel" %% "cats-collections-core" % "0.9.7",
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.typelevel" %% "cats-effect" % "3.5.1",
      "org.typelevel" %% "cats-effect-kernel" % "3.5.1",
      "org.typelevel" %% "cats-kernel" % "2.9.0",
      "org.typelevel" %% "log4cats-core" % "2.6.0",
      "org.typelevel" %% "log4cats-slf4j" % "2.6.0",
    ),
    unusedCompileDependenciesFilter -= moduleFilter("io.github.iltotore", "iron"),
    unusedCompileDependenciesFilter -= moduleFilter("io.github.iltotore", "iron-cats"),
    dockerApiVersion := com.typesafe.sbt.packager.docker.DockerApiVersion.parse("1.43"),
    dockerBaseImage := "eclipse-temurin:17-jre",
    dockerExposedPorts ++= Seq(8080),
    Universal / maintainer := "https://github.com/etorres/fleet4s",
  )
  .dependsOn(commons % "test->test;compile->compile")
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .disablePlugins(ScalafixPlugin) // disabling until all dependencies support Scala 3.3.x

lazy val MUnitFramework = new TestFramework("munit.Framework")
lazy val warts = Warts.unsafe.filter(_ != Wart.DefaultArguments)

lazy val baseSettings: Project => Project = _.settings(
  idePackagePrefix := Some("es.eriktorr"),
  Global / excludeLintKeys += idePackagePrefix,
  Compile / doc / sources := Seq(),
  Compile / compile / wartremoverErrors ++= warts,
  Test / compile / wartremoverErrors ++= warts,
  libraryDependencies ++= Seq(
    "com.47deg" %% "scalacheck-toolbox-datetime" % "0.7.0" % Test,
    "io.chrisdavenport" %% "cats-scalacheck" % "0.3.2" % Test,
    "org.scalameta" %% "munit" % "0.7.29" % Test,
    "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
    "org.typelevel" %% "scalacheck-effect" % "1.0.4" % Test,
    "org.typelevel" %% "scalacheck-effect-munit" % "1.0.4" % Test,
  ),
  Test / fork := true,
  Test / envVars := Map(
    "SBT_TEST_ENV_VARS" -> "true",
    "FLEET4S_HTTP_HOST" -> "localhost",
    "FLEET4S_HTTP_PORT" -> "8000",
    "FLEET4S_JDBC_CONNECTIONS" -> "2:4",
    "FLEET4S_JDBC_CONNECT_URL" -> "jdbc:postgresql://localhost:3306/database_name",
    "FLEET4S_JDBC_PASSWORD" -> "database_password",
    "FLEET4S_JDBC_USERNAME" -> "database_username",
  ),
  Test / testFrameworks += MUnitFramework,
  Test / testOptions += Tests.Argument(MUnitFramework, "--exclude-tags=online"),
)

lazy val root = project
  .in(file("."))
  .aggregate(commons, `fleet-control`)
  .settings(
    name := "fleet4s",
    Compile / doc / sources := Seq(),
    publish := {},
    publishLocal := {},
  )
