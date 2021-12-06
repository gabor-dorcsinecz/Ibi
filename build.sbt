name := "quiz"
organization := "com.ibraaheem.muhammad"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.6"

lazy val root = Project("quiz", file("."))
  .enablePlugins(
    play.sbt.PlayScala
  )
  .settings(
    libraryDependencies ++= compileLibs ++ testLibs
  )


val compileLibs = Seq(
  guice,
  "org.typelevel"               %% "cats-core"                      % "2.3.1",
  "org.reactivemongo"           %% "play2-reactivemongo"            % "1.0.7-play28",
  "org.reactivemongo"           %% "reactivemongo-play-json-compat" % "1.0.7-play28",
  "org.reactivemongo"           %% "reactivemongo-bson-compat"      % "0.20.13",
)

val testLibs = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
  )





// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.ibraaheem.muhammad.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.ibraaheem.muhammad.binders._"
