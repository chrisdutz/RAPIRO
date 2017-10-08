package de.codecentric.iot.rapiro.streams

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import de.codecentric.iot.rapiro.vision.actors.VisionActor.UpdateScene
import de.codecentric.iot.rapiro.vision.model.Block
import org.json4s._
import org.json4s.native.Serialization

/**
  * Created by christoferdutz on 19.10.16.
  */
class SceneDebugActor extends Actor with LazyLogging {

  implicit val formats:Formats = Serialization.formats(NoTypeHints)

  override def receive: Receive = {
    case event:UpdateScene =>
      if((event.scene != null) && (event.scene.getBlocks != null) && !event.scene.getBlocks.isEmpty) {
        logger.trace("detected {} items", event.scene.getBlocks.length)
        for(item:Block <- event.scene.getBlocks) {
          logger.trace(s"  ${item.getX}, ${item.getY}, ${item.getWidth}, ${item.getHeight}")
        }
      }
  }

}
