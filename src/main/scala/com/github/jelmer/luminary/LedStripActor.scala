package com.github.jelmer.luminary

import akka.actor.{Actor, ActorLogging, Props}
import com.pi4j.io.gpio._

object LedStripActor {
  case object TurnOn

  case object TurnOff

  def props: Props = Props[LedStripActor]
}
class LedStripActor extends Actor with ActorLogging {
  import com.github.jelmer.luminary.LedStripActor._
  import context._

  lazy val pin: GpioPinDigitalOutput = {
    val p = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_01, "Light", PinState.LOW)
    p.setShutdownOptions(true, PinState.LOW)
    p
  }

  def on: Receive =  {
    case TurnOn =>
      log.info("already on")
    case TurnOff =>
      pin.low()
      become(off)
  }

  def off: Receive = {
    case TurnOn =>
      pin.high()
      become(on)
    case TurnOff =>
      log.info("already off")
  }

  override def receive: Receive =  off

}
