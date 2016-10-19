package de.codecentric.iot.rapiro.streams

import akka.actor.Actor

/**
  * Created by christoferdutz on 19.10.16.
  */
class DebugActor extends Actor {

  override def receive: Receive = {
    case event:Object => println(event)
  }

}
