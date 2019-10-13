package com.github.jelmer.luminary

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.jelmer.luminary.LedStripActor.{TurnOff, TurnOn}
import com.github.jelmer.luminary.PirSensorActor.{MotionDetected, NoMotionDetected, Subscribe}

object OrchestratorActor {

  def props(ledStripActor: ActorRef, pirSensorActor: ActorRef): Props =
    Props(new OrchestratorActor(ledStripActor, pirSensorActor))
}

class OrchestratorActor(ledStripActor: ActorRef, pirSensorActor: ActorRef) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    pirSensorActor ! Subscribe
  }

  override def receive: Receive = {
    case MotionDetected =>
      log.debug("Motion detected. Turning on light.")

      ledStripActor ! TurnOn

    case NoMotionDetected =>
      log.debug("Motion detected. Turning off light.")

      ledStripActor ! TurnOff
  }
}
