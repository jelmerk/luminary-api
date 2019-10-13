package com.github.jelmer.luminary

import com.pi4j.io.gpio.{GpioController, GpioFactory}

object GpioFactoryHolder {

  // creating this singleton is not thread safe in pi4j, why oh why ??
  val gpioFactory: GpioController = GpioFactory.getInstance()
}
