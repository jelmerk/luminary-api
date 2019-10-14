package com.github.jelmer.luminary


import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.jelmer.luminary.LedStripActor.{TurnOff, TurnOn}
import com.github.jelmer.luminary.PirSensorActor.{MotionDetected, Subscribe}
import com.github.jelmer.luminary.SpeakerActor.{Play, StopPlayback}

object OrchestratorActor {

  case object TriggerEnd

  def props(ledStripActor: ActorRef, pirSensorActor: ActorRef, speakerActor: ActorRef): Props =
    Props(new OrchestratorActor(ledStripActor, pirSensorActor, speakerActor))
}

class OrchestratorActor(ledStripActor: ActorRef, pirSensorActor: ActorRef, speakerActor: ActorRef)
  extends Actor with ActorLogging {

  import context.dispatcher
  import OrchestratorActor._

  private val mediaUrl = Thread.currentThread.getContextClassLoader.getResource("alert.wav").toString

  override def preStart(): Unit = {
    pirSensorActor ! Subscribe
  }

  def active: Receive = {
    case TriggerEnd =>
      log.info("Trigger end")
      speakerActor ! StopPlayback
      ledStripActor ! TurnOff
      context.become(inactive)
  }

  def inactive: Receive = {
    case MotionDetected =>
      log.debug("Motion detected. Turning on light.")

      speakerActor ! Play(mediaUrl)
      ledStripActor ! TurnOn

      context.system.scheduler.scheduleOnce(10.second, self, TriggerEnd)

      context.become(active)
  }

  override def receive: Receive = inactive
}
