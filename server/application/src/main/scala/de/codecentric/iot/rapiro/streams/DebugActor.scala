package de.codecentric.iot.rapiro.streams

import akka.actor.Actor
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

/**
  * Created by christoferdutz on 19.10.16.
  */
class DebugActor extends Actor {
  implicit val formats = Serialization.formats(NoTypeHints)

  override def receive: Receive = {
    case event:Object =>
      val jsonString = write(event)
      println(jsonString)
  }

}
