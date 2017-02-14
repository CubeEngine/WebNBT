enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

name := "WebNBT"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "scalatags" % "0.6.3"
)

npmDependencies in Compile := Seq(
  "pako" -> "1.0.4"
)
