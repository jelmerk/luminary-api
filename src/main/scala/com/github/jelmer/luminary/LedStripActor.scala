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

  private val pin: GpioPinDigitalOutput = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_01, "Light", PinState.LOW)
  pin.setShutdownOptions(true, PinState.LOW)

  def on: Receive =  {
    case TurnOn =>
      log.info("already on")
    case TurnOff =>
      pin.low()
      context.become(off)
  }

  def off: Receive = {
    case TurnOn =>
      pin.high()
      context.become(on)
    case TurnOff =>
      log.info("already off")
  }

  override def receive: Receive =  off

}
