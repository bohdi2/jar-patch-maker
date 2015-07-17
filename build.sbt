organization := "org.bodhi" 

name := "jar-patch"

version := "1.0"

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "commons-cli" % "commons-cli" % "1.2",
  "joda-time" % "joda-time" % "1.6",
  "junit" % "junit" % "3.8.2" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
