name := "tutelar-example-todoapp-akkahttp"

version := "0.1"

scalaVersion := "2.13.1"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= {
  val akkaHttpV = "10.1.11"
  val akkaV     = "2.6.1"
  val slickV    = "3.3.2"
  val jwtV      = "4.2.0"
  Seq(
    //db
    "com.typesafe.slick" %% "slick"          % slickV,
    "com.typesafe.slick" %% "slick-hikaricp" % slickV,
    "org.postgresql"     % "postgresql"      % "42.2.9",
    "org.flywaydb"       % "flyway-core"     % "6.1.2",
    //akka + json
    "com.typesafe.akka" %% "akka-http"            % akkaHttpV,
    "com.typesafe.akka" %% "akka-stream"          % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "ch.megard"         %% "akka-http-cors"       % "0.4.2",
    "com.pauldijou"     %% "jwt-core"             % jwtV,
    "com.pauldijou"     %% "jwt-spray-json"       % jwtV,
    //misc
    "org.typelevel"         %% "cats-core"  % "2.1.0",
    "com.github.pureconfig" %% "pureconfig" % "0.12.1",
    //logging
    "net.logstash.logback" % "logstash-logback-encoder" % "6.3",
    "ch.qos.logback"       % "logback-classic"          % "1.2.3",
    "org.slf4j"            % "jul-to-slf4j"             % "1.7.30",
    "com.typesafe.akka"    %% "akka-slf4j"              % akkaV,
    "org.codehaus.janino"  % "janino"                   % "3.1.0"
  )
}
