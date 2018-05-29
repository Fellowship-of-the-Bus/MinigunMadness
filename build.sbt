val os = System.getProperty("os.name").split(" ")(0).toLowerCase match {
  case "linux" => "linux"
  case "mac" => "macosx"
  case "windows" => "windows"
  case "sunos" => "solaris"
  case x => x
}

val separator = System.getProperty("os.name").split(" ")(0).toLowerCase match {
  case "linux" => ":"
  case "mac" => ":"
  case "windows" => ";"
  case "sunos" => ":"
  case x => ":"
}

lazy val commonSettings = Seq(
  name := "Minigun Madness",
  organization := "com.github.fellowship_of_the_bus",
  version := "1.0",
  scalaVersion := "2.12.6",
  fork := true,
  javacOptions ++= Seq(
    "-encoding", "utf8",
    "-source", "1.8",
    "-target", "1.8",
    "-Xlint"
  ),
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding", "utf8",
    "-target:jvm-1.8",
    "-opt:l:inline",
    "-opt-inline-from:**",
    "-Xlint",
    // "-Xfatal-warnings",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-value-discard",
    "-Xlint:-unused,_",
    // "-Ywarn-unused:imports",
  ),
  javaOptions ++= Seq(
    s"-Djava.library.path=${System.getProperty("java.library.path")}${separator}./src/main/resources/natives/${os}"
  ),
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    Resolver.url(
      "sbt-plugin-releases",
      new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
    )(Resolver.ivyStylePatterns)
  ),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "junit" % "junit" % "4.12" % "test",
    "com.propensive" %% "rapture-json-jackson" % "2.0.0-M9",
    "com.github.pathikrit" %% "better-files" % "3.5.0",
    "com.github.fellowship_of_the_bus" %% "fellowship-of-the-bus-lib" % "0.4-SNAPSHOT" changing(),
    "com.github.fellowship_of_the_bus" %% "fellowship-of-the-bus-slick2d-lib" % "0.2-SNAPSHOT" changing(),
    "org.jbox2d" % "jbox2d-library" % "2.2.1.1"
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
