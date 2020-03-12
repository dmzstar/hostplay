name := """domain"""

/**
lazy val domain = (project in file(".")).enablePlugins(PlayScala,PlayEbean)
scalaVersion := "2.12.8"
*/

Common.settings


libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.199"
libraryDependencies += caffeine
libraryDependencies ++= Common.testDeps
