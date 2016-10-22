package de.codecentric.iot.rapiro.streams

import akka.actor.Actor
import de.codecentric.iot.rapiro.vision.actors.VisionActor.UpdateScene
import org.json4s._
import org.json4s.native.Serialization

/**
  * Created by christoferdutz on 19.10.16.
  */
class SceneDebugActor extends Actor {
  implicit val formats = Serialization.formats(NoTypeHints)

  override def receive: Receive = {
    case event:UpdateScene =>
      if((event.scene != null) && (event.scene.getBlocks != null) && !event.scene.getBlocks.isEmpty) {
        println("got " + event.scene.getBlocks + " blocks")
      }
  }

}
