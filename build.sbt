import com.typesafe.sbt.packager.jdkpackager._

lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion = "2.6.0-M1"

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging)
  .settings(
    inThisBuild(List(
      organization    := "com.github.jelmerk",
      scalaVersion    := "2.12.7",
      scalacOptions   := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8"),
      version         := "0.1"
    )),
    name := "luminary-api",

    javaOptions in Universal ++= Seq(
      "-Djava.net.preferIPv4Stack=true",
      "-Dio.netty.tryReflectionSetAccessible=false",
      "-J-XX:+UseConcMarkSweepGC"
    ),
    
    libraryDependencies ++= Seq(
      "ch.qos.logback"    %  "logback-classic"      % "1.1.2",
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-slf4j"           % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "com.pi4j"          %  "pi4j-gpio-extension"  % "1.2",
      "io.netty"          %  "netty-all"            % "4.1.24.Final",
      "org.slf4s"         %% "slf4s-api"            % "1.7.25",
      
      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.mockito"       %  "mockito-core"         % "2.28.2"        % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test
    )
  )
