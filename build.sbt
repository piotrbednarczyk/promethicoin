name := "promethicoin"

lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .aggregate(promethicoinModel)
  .dependsOn(promethicoinModel)
  .settings(
    commonSettings
  )

lazy val promethicoinModel = (project in file("promethicoinModel"))
  .settings(
    commonSettings
  )

libraryDependencies ++= Seq(
  javaWs,
  "ws.wamp.jawampa" % "jawampa-core" % "0.4.2",
  "ws.wamp.jawampa" % "jawampa-netty" % "0.4.2"

)

scriptClasspath := Seq("*")