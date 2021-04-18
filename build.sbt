name := "star"

version := "0.1"

scalaVersion := "2.12.13"
val akkaV = "2.6.10"
val slickPgVersion = "0.18.0"
val slickVersion = "3.3.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.2.1",
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaV,
  // logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  // typesafe
  "com.typesafe.play" %% "play-json" % "2.7.3",

  //Postgres
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.github.tminglei" %% "slick-pg" % slickPgVersion,
  "org.postgresql" % "postgresql" % "42.2.8",
  "org.flywaydb" % "flyway-core" % "7.4.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.2"
)
