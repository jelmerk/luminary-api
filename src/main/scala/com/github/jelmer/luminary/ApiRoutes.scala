package com.github.jelmer.luminary

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.github.jelmer.luminary.LedStripActor.{TurnOff, TurnOn}


trait ApiRoutes {

  implicit def system: ActorSystem

  def ledStripActor: ActorRef

  def pirSensorActor: ActorRef

  lazy val apiRoute: Route = {

    // TODO just for testing need to design a proper api

    pathPrefix("light") {
      concat(
        path("enable") {
          ledStripActor ! TurnOn
          complete(NoContent)
        },
        path("disable") {
          ledStripActor ! TurnOff
          complete(NoContent)
        }
      )
    }

  }


}
