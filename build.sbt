name := "swagger2postman"

organization := "com.josephpconley"

version := "1.0"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.4",
  "org.rogach" %% "scallop" % "0.9.5",
  "com.stackmob" %% "newman" % "1.3.5"
)