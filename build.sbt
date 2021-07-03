enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

name := "WebNBT"

version := "0.1-SNAPSHOT"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "1.1.0",
)

scalaJSUseMainModuleInitializer := true

Compile / npmDependencies := Seq(
  "pako" -> "2.0.3"
)
