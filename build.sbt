name := "sbt-assembly-shading"
version := "1.0-SNAPSHOT"
scalaVersion := "2.11.11"
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
scalacOptions := Seq("-target:jvm-1.8")

lazy val circeVersion = "0.8.0"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-generic-extras",
  "io.circe" %% "circe-optics",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

assemblyShadeRules in assembly := Seq(
  ShadeRule.rename("shapeless.**" -> "shaded_shapeless.@1")
    .inLibrary("com.chuusai" % "shapeless_2.11" % "2.3.2")
    .inLibrary("io.circe" % "circe-generic_2.11" % circeVersion)
    .inLibrary("io.circe" % "circe-generic-extras_2.11" % circeVersion).inProject
)
