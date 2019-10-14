package com.github.jelmer.luminary

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.github.jelmer.luminary.Main.system
import javafx.beans.value
import javafx.beans.value.ObservableValue
import javafx.scene.media.{Media, MediaPlayer}

import scala.concurrent.ExecutionContext

object SpeakerActor {
  case class Play(url: String)

  case object EndOfMedia

  case object Busy

  def props: Props = Props[SpeakerActor]
}
class SpeakerActor extends Actor with ActorLogging {

  import SpeakerActor._

  def playing(mediaPlayer: MediaPlayer): Receive = {

    case Play(url) =>
      sender ! Busy
      log.info(s"Ignoring request to play $url. already playing")
    case EndOfMedia =>
      log.debug("End of media file reached becoming idle")
      mediaPlayer.dispose()
      context.become(idle)
  }

  def idle: Receive = {
    case Play(url) =>
      val hit = new Media(url)
      val mediaPlayer = new MediaPlayer(hit)
      mediaPlayer.setOnEndOfMedia(() =>  self ! EndOfMedia)
      mediaPlayer.play()
      context.become(playing(mediaPlayer))
  }

  override def receive: Receive = idle

}


object Test {


  def main(args: Array[String]): Unit = {

    import SpeakerActor._

//    val hit = new Media(new File(bip).toURI.toString)
    com.sun.javafx.application.PlatformImpl.startup(() => ())


    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executor: ExecutionContext = system.dispatcher


    val speakerActor: ActorRef = system.actorOf(SpeakerActor.props, "speakerActor")

    val mediaUrl = Thread.currentThread().getContextClassLoader.getResource("alert.wav").toString

    speakerActor ! Play(mediaUrl)

    Thread.sleep(10000)

    speakerActor ! Play(mediaUrl)

    Thread.sleep(1000)

    speakerActor ! Play(mediaUrl)

    Thread.sleep(1000000)

  }
}