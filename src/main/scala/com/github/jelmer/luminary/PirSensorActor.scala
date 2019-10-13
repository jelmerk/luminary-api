package com.github.jelmer.luminary

import scala.collection.immutable.HashSet

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pi4j.io.gpio.event.{GpioPinDigitalStateChangeEvent, GpioPinListenerDigital}
import com.pi4j.io.gpio.{GpioPinDigitalInput, PinPullResistance, RaspiPin}

import GpioFactoryHolder.gpioFactory

object PirSensorActor {

  case object Subscribe

  case object Unsubscribe

  case object MotionDetected

  case object NoMotionDetected

  def props: Props = Props[PirSensorActor]

}

class PirSensorActor extends Actor with ActorLogging {

  import PirSensorActor._

  private lazy val sensor: GpioPinDigitalInput = gpioFactory.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN)
  sensor.setShutdownOptions(true)
  sensor.addListener(new GpioPinListenerDigital() {
    override def handleGpioPinDigitalStateChangeEvent(event: GpioPinDigitalStateChangeEvent): Unit = {
      self ! event
    }
  })

  override def receive: Receive = onMessage(HashSet.empty[ActorRef], detected = false)

  private def onMessage(subscribers: HashSet[ActorRef], detected: Boolean): Receive = {
    case e: GpioPinDigitalStateChangeEvent if e.getState.isHigh && !detected =>
      subscribers.foreach { _ ! MotionDetected }
      log.debug("motion detected")
      context.become(onMessage(subscribers, detected = true))

    case e: GpioPinDigitalStateChangeEvent if e.getState.isLow && detected =>
      subscribers.foreach { _ ! NoMotionDetected }
      log.debug("no motion detected")
      context.become(onMessage(subscribers, detected = false))

    case Subscribe =>
      context.become(onMessage(subscribers + sender, detected))

    case Unsubscribe =>
      context.become(onMessage(subscribers - sender, detected))
  }
}
