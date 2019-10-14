package com.github.jelmer.luminary

import com.sun.javafx.application.PlatformImpl
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Main extends App with ApiRoutes with Logging {

  PlatformImpl.startup(() => ()) // needed to use the media library lib

  val config: Config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executor: ExecutionContext = system.dispatcher

  val ledStripActor: ActorRef = system.actorOf(LedStripActor.props, "ledStripActor")
  val pirSensorActor: ActorRef = system.actorOf(PirSensorActor.props, "pirSensorActor")
  val speakerActor: ActorRef = system.actorOf(SpeakerActor.props, "speakerActor")

  val orchestratorActor: ActorRef = system.actorOf(OrchestratorActor.props(ledStripActor, pirSensorActor, speakerActor), "orchestratorActor")

  lazy val routes: Route = apiRoute

  val serverBinding: Future[Http.ServerBinding] = Http()
    .bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))

  serverBinding.onComplete {
    case Success(bound) =>
      log.info(s"Http endpoint online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      log.error(s"Server could not start!", e)
      system.terminate()
  }

}
