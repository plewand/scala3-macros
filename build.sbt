val scala3Version = "3.0.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-macros",
    version := "0.1.0",

    // Additional check useful during development
    scalacOptions ++= Seq(
      "-Xcheck-macros"
    ),

    scalaVersion := scala3Version,
  )
