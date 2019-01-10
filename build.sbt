name := "htm-stacktrace-example"

version := "0.1"

scalaVersion := "2.12.8"

resolvers in ThisBuild += "Metaware" at "http://metaware.us/maven3/"

libraryDependencies += "org.numenta" % "htm.java" % "0.6.13"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test