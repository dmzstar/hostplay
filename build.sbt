name := """hostplay"""
organization := "com.weifan"
maintainer := "dmzstar@weifan.com"
version := "1.0-SNAPSHOT"

Common.settings

//lazy val root = (project in file(".")).enablePlugins(PlayScala,PlayMinimalJava,PlayEbean)

//lazy val front = (project in file("modules/front")).enablePlugins(PlayScala)
//lazy val admin = (project in file("modules/admin-boot")).enablePlugins(PlayScala)
lazy val domain = (project in file("modules/domain")).enablePlugins(PlayScala,PlayEbean)

lazy val upload = (project in file("modules/upload")).enablePlugins(PlayScala)

/**
lazy val member = (project in file("modules/member")).enablePlugins(PlayScala,PlayEbean)
  .dependsOn(front)
  .dependsOn(adminBoot)
  .dependsOn(domain)

 */

/**
lazy val root = (project in file(".")).enablePlugins(PlayScala,PlayEbean)
  .dependsOn(front).aggregate(front)
  .dependsOn(admin).aggregate(admin)
  .dependsOn(domain).aggregate(domain)
*/

lazy val root = (project in file(".")).enablePlugins(PlayScala,PlayEbean)
  //.dependsOn(front).aggregate(front)
  .dependsOn(upload).aggregate(upload)
  .dependsOn(domain).aggregate(domain)
  //.dependsOn(admin).aggregate(admin)
  //.dependsOn(front).aggregate(front)

//忽略源码，文档
//sources in (Compile, doc) := Seq.empty
//publishArtifact in (Compile, packageDoc) := false


val javaVersion = System.getProperty("java.version")


//begin java9+ deps
//libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.4.0-b180830.0359"
//libraryDependencies += "com.sun.xml.bind" % "jaxb-impl" % "2.4.0-b180830.0438"
//libraryDependencies += "jakarta.activation" % "jakarta.activation-api" % "1.2.1"
//libraryDependencies += "com.sun.istack" % "istack-commons-runtime" % "3.0.8"


//end


libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.199"
libraryDependencies += caffeine
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "8.0.0"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "8.0.0"



libraryDependencies ++= Common.testDeps

//PlayKeys.externalizeResources := true
//PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "public" / "uploads"

//unmanagedResourceDirectories in Assets += "/uploads"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.weifan.ferriercontent.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.weifan.ferrierblog.binders._"
