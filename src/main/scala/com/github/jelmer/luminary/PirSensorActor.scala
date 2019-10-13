package com.github.jelmer.luminary

import akka.actor.{Actor, ActorLogging, Props}

object PirSensorActor {

  case object Enable

  case object Disable

  case object Subscribe

  case object Unsubscribe

  def props: Props = Props[PirSensorActor]

}

class PirSensorActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case m: AnyRef =>
      log.info(s"Received messge $m")
  }
}
