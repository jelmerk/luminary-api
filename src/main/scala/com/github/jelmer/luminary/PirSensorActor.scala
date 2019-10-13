package com.github.jelmer.luminary

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import com.pi4j.io.gpio.event.{GpioPinDigitalStateChangeEvent, GpioPinListenerDigital}
import com.pi4j.io.gpio.{GpioFactory, GpioPinDigitalInput, PinPullResistance, RaspiPin}

import scala.collection.immutable.HashSet
import scala.concurrent.duration._

object PirSensorActor {

  case object Enable

  case object Disable

  case object Subscribe

  case object Unsubscribe

  case object FetchState

  def props: Props = Props[PirSensorActor]

}

class PirSensorActor extends Actor with ActorLogging {

  import PirSensorActor._
  import context._

  private val subscribers: HashSet[ActorRef] = HashSet.empty[ActorRef]

  private lazy val sensor: GpioPinDigitalInput = {
    val s = GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN)
    s.setShutdownOptions(true)


    s.addListener(new GpioPinListenerDigital() {


//      @Override
//      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
//        // display pin state on console
//        System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
//      }
      override def handleGpioPinDigitalStateChangeEvent(event: GpioPinDigitalStateChangeEvent): Unit = {
        event.getState

      }
    })


    s
  }

  val fetchTick: Cancellable = context.system.scheduler.schedule(Duration.Zero, 1.second, self, FetchState)

  override def receive: Receive = onMessage(subscribers)

  private def onMessage(subscribers: HashSet[ActorRef]): Receive = {
    case FetchState =>


    case Subscribe =>
      context.become(onMessage(subscribers + sender))

    case Unsubscribe =>
      context.become(onMessage(subscribers - sender))
  }
}
