val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    organization := "com.simplehosting",

    name := "simple-hosting.juggernaut.worker",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.yaml" % "snakeyaml" % "2.2",
    libraryDependencies += "com.fasterxml.jackson.module"     %% "jackson-module-scala" % "2.14.1",


    // DI
    libraryDependencies += "com.google.inject" % "guice" % "7.0.0",
    libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0",

    // Redis
    libraryDependencies += "redis.clients" % "jedis" % "4.3.1",

    // RabbitMQ
    libraryDependencies += "com.rabbitmq" % "amqp-client" % "5.20.0",

    // Logging
    libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.10",
    libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "2.0.10"
  )
