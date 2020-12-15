import sbt._
import sbt.Keys._

val akkaVersion     = "2.6.10"
val jdbcVersion     = "4.0.0"
val postgreVersion  = "42.2.16"

lazy val root =
  Project(id = "root", base = file("."))
    .enablePlugins(ScalafmtPlugin)
    .settings(
      name := "root",
      skip in publish := true,
      scalafmtOnCompile := true,
    )
    .withId("root")
    .settings(commonSettings)
    .aggregate(
      taxiRidePipeline,
      datamodel,
      akkastreams
    )

lazy val taxiRidePipeline = appModule("taxi-ride-pipeline")
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(commonSettings)
  .settings(
    name := "taxi-ride-proto",
  )

lazy val datamodel = appModule("datamodel")
  .enablePlugins(CloudflowLibraryPlugin)
  .settings(
    commonSettings,
  )

lazy val akkastreams =  (project in file("akkastreams"))
  .enablePlugins(CloudflowAkkaPlugin)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka"         %% "akka-persistence-typed"       % akkaVersion,
      "com.typesafe.akka"         %% "akka-serialization-jackson"   % akkaVersion,
      "com.typesafe.akka"         %% "akka-persistence-query"       % akkaVersion,
      "com.lightbend.akka"        %% "akka-persistence-jdbc"        % jdbcVersion,
      "org.postgresql"            % "postgresql"                    % postgreVersion,
      "com.typesafe.akka"         %% "akka-http-spray-json"         % "10.1.12",
      "ch.qos.logback"            %  "logback-classic"              % "1.2.3",
      "org.scalatest"             %% "scalatest"                    % "3.0.8"       % "test"
    )
  )
  .dependsOn(datamodel)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(
      name := moduleID
    )
    .withId(moduleID)
    .settings(commonSettings)
}

lazy val commonSettings = Seq(
  organization := "com.lightbend.cloudflow",
  headerLicense := Some(HeaderLicense.ALv2("(C) 2016-2020", "Lightbend Inc. <https://www.lightbend.com>")),
  scalaVersion := "2.12.11",
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.8",
    "-Xlog-reflective-calls",
    "-Xlint",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-deprecation",
    "-feature",
    "-language:_",
    "-unchecked"
  ),

  scalacOptions in (Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
)
