package de.codecentric.iot.rapiro.objecttracker

import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging._
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent}
import de.codecentric.iot.rapiro.vision.actors.VisionActor.UpdateScene
import de.codecentric.iot.rapiro.vision.model.{Block, Scene}
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope("prototype")
@Component("objectTrackerActor")
class ObjectTrackerActor extends Actor with LazyLogging {

  var listeners: List[ActorRef] = List[ActorRef]()

  val numScenes: Int = 6
  var lastScenes: ListBuffer[Scene] = ListBuffer[Scene]()

  override def receive: Receive = {
    case event:AddListenerEvent =>
      listeners = event.getActorRef :: listeners
    case event:RemoveListenerEvent =>
      listeners = listeners.filter(_ == event.getActorRef)
    case event:UpdateScene =>
      try {
        // Add the scene to the list
        lastScenes += event.scene
        // If the list now contains more elements than we want,
        // cut off any excess elements.
        while (lastScenes.length > numScenes) {
          lastScenes -= lastScenes.head
        }

        if (listeners.nonEmpty) {
          val updatedScene: UpdateScene = UpdateScene(getProcessedScene)
          listeners.foreach(target => target ! updatedScene)
        }
      } catch {
        case e: Exception => logger.error("An error occurred while processing incoming update event.", e)
      }
  }

  def getProcessedScene:Scene = {
    var mergedBlocks: List[Block] = List[Block]()
    val newScene: Scene = new Scene()
    lastScenes.foreach(scene => {
      if(scene.getBlocks != null) {
        scene.getBlocks.foreach(block => {
          mergedBlocks = new BlockMerger().mergeBlocks(block, mergedBlocks)
        })
      }
      newScene.setWidth(scene.getWidth)
      newScene.setHeight(scene.getHeight)
      newScene.setTime(scene.getTime)
    })
    newScene.setBlocks((mergedBlocks map(block => block)).toArray)
    newScene
  }

}

class BlockMerger {
  def mergeBlocks(block: Block, list: List[Block]): List[Block] = {
    intMergeBlocks(block, list, List[Block]())
  }

  @tailrec private def intMergeBlocks(block: Block, list: List[Block], merged: List[Block]): List[Block] = {
    // If the list is empty, just add the current block to the merged list.
    if (list.isEmpty) {
      block :: merged
    }
    // If the list contains elements, check if the current block intersects with the
    // first element, if not recurse with the rest.
    else {
      // If the two block intersect, replace that
      // block with a block that is merged with the current block.
      if(list.head.intersects(block)) {
        intMergeBlocks(list.head.unite(block), list.tail, merged)
      }
      // If they don't intersect, just add the block to the merged list.
      else {
        val newMerged = list.head :: merged
        intMergeBlocks(block, list.tail, newMerged)
      }
    }
  }
}



