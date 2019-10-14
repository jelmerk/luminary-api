package com.github.jelmer.luminary

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.jelmer.luminary.LedStripActor.{TurnOff, TurnOn}
import com.github.jelmer.luminary.PirSensorActor.{MotionDetected, NoMotionDetected, Subscribe}
import com.github.jelmer.luminary.SpeakerActor.Play

object OrchestratorActor {

  def props(ledStripActor: ActorRef, pirSensorActor: ActorRef, speakerActor: ActorRef): Props =
    Props(new OrchestratorActor(ledStripActor, pirSensorActor, speakerActor))
}

class OrchestratorActor(ledStripActor: ActorRef, pirSensorActor: ActorRef, speakerActor: ActorRef)
  extends Actor with ActorLogging {

  private val mediaUrl = Thread.currentThread().getContextClassLoader.getResource("alert.wav").toString

  override def preStart(): Unit = {
    pirSensorActor ! Subscribe
  }

  override def receive: Receive = {
    case MotionDetected =>
      log.debug("Motion detected. Turning on light.")

      speakerActor ! Play(mediaUrl)
      ledStripActor ! TurnOn

    case NoMotionDetected =>
      log.debug("Motion detected. Turning off light.")

      ledStripActor ! TurnOff
  }
}
