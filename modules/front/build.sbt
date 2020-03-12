name := """front"""

Common.settings


libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.199"
libraryDependencies += caffeine
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "8.0.0"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "8.0.0"



libraryDependencies ++= Common.testDeps




