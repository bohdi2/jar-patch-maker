
organization := "org.bodhi"

name := "jar-patch"

version := "1.0"

enablePlugins(JavaAppPackaging)


scalaVersion := "2.11.6"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "commons-cli" % "commons-cli" % "1.3.1",
  "joda-time" % "joda-time" % "1.6",
  "junit" % "junit" % "3.8.2" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.scala-lang" % "scala-compiler" % "2.11.6"
)

