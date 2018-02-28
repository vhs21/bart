name := "bart"

version := "0.1"

scalaVersion := "2.12.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  jdbc,
  "mysql" % "mysql-connector-java" % "8.0.8-dmr",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "org.mindrot" % "jbcrypt" % "0.4",
  "com.jason-goodwin" % "authentikat-jwt_2.12" % "0.4.5"
)
