package com.github.jelmer.luminary

import akka.actor.{Actor, ActorLogging, Props}
import javafx.scene.media.{Media, MediaPlayer}

object SpeakerActor {
  case object StopPlayback

  case class Play(url: String)

  case object EndOfMedia

  case object Busy

  def props: Props = Props[SpeakerActor]
}

class SpeakerActor extends Actor with ActorLogging {

  import SpeakerActor._

  def playing(mediaPlayer: MediaPlayer): Receive = {

    case Play(url) =>
      log.info(s"Ignoring request to play $url. already playing")
      sender ! Busy

    case EndOfMedia | StopPlayback =>
      log.info("Stopping audio playback")
      mediaPlayer.dispose()
      context.become(idle)
  }

  def idle: Receive = {
    case Play(url) =>
      log.info(s"Playing $url")
      val hit = new Media(url)
      val mediaPlayer = new MediaPlayer(hit)
      mediaPlayer.setOnEndOfMedia(() =>  self ! EndOfMedia)
      mediaPlayer.play()
      context.become(playing(mediaPlayer))
  }

  override def receive: Receive = idle

}
