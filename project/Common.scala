import sbt._
import Keys._

object Common {

  val settings: Seq[Setting[_]] = Seq(
    scalaVersion := "2.13.1"
  )

  val testDeps = Seq("org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test)

}
