package example

import sbt._
import Keys._
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform._

import scala.collection.JavaConverters._

object ExampleBuild extends Build {

  val algebirdVersion = "0.11.0"
  val bijectionVersion = "0.8.1"
  val quasiquotesVersion = "2.0.1"
  val scalaCheckVersion = "1.12.2"
  val scalaTestVersion = "2.2.4"
  val slf4jVersion = "1.6.6"

  val sharedSettings = Project.defaultSettings ++ scalariformSettings ++ Seq(
    organization := "com.example",

    scalaVersion := "2.11.7",

    ScalariformKeys.preferences := formattingPreferences,

    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "org.slf4j" % "slf4j-log4j12" % slf4jVersion % "test"),

    resolvers ++= Seq(
      "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
      "maven central" at "https://repo.maven.apache.org/maven2",
      "releases" at "https://oss.sonatype.org/content/repositories/releases",
      "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"),

    updateOptions := updateOptions.value.withConsolidatedResolution(true),

    updateOptions := updateOptions.value.withCachedResolution(true),

    scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:implicitConversions", "-language:higherKinds", "-language:existentials"),

    // Enables full stack traces in scalatest
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

    // Publishing options:
    publishMavenStyle := true,

    publishArtifact in Test := false,

    publishTo <<= version { v =>
      Some(
        if (v.trim.endsWith("SNAPSHOT"))
          Opts.resolver.sonatypeSnapshots
        else
          Opts.resolver.sonatypeStaging)
    },

    pomExtra := (
      <url>https://github.com/ianoc/ExampleScalaProject</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
          <distribution>repo</distribution>
          <comments>A business-friendly OSS license</comments>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:ianoc/ExampleScalaProject.git</url>
        <connection>scm:git:git@github.com:ianoc/ExampleScalaProject.git</connection>
      </scm>
      <developers>
        <developer>
          <id>ianoc</id>
          <name>Ian O Connell</name>
          <url>http://twitter.com/0x138</url>
        </developer>
      </developers>))

  lazy val example = Project(
    id = "example",
    base = file("."),
    settings = sharedSettings).settings(
      test := {},
      publish := {}, // skip publishing for this root project.
      publishLocal := {}).aggregate(
        exampleCore)

  lazy val formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences().
      setPreference(AlignParameters, false).
      setPreference(PreserveSpaceBeforeArguments, true)
  }

  def module(name: String) = {
    val id = "example-%s".format(name)
    Project(id = id, base = file(id), settings = sharedSettings ++ Seq(
      Keys.name := id))
  }

  lazy val exampleCore = module("core").settings(
    libraryDependencies <++= (scalaVersion) { scalaVersion =>
      Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion,
        "com.twitter" %% "algebird-core" % algebirdVersion,
        "com.twitter" %% "bijection-core" % bijectionVersion
        )
    },
    initialCommands in console := "import com.example._",
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full))

}
