package de.codecentric.iot.rapiro.vision.actors

import java.util.Calendar

import akka.actor.{Actor, ActorRef}
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent, UpdateEvent}
import de.codecentric.iot.rapiro.vision.actors.VisionActor.UpdateScene
import de.codecentric.iot.rapiro.vision.adapter.VisionAdapter
import de.codecentric.iot.rapiro.vision.model.{Block, ColorBlock, Scene}
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.concurrent.duration.DurationInt

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope ("prototype")
@Component ("visionActor")
class VisionActor() extends Actor with InitializingBean {
  import context.dispatcher

  @Autowired private val visionAdapter: VisionAdapter = null

  var listeners:List[ActorRef] = List[ActorRef]()

  override def afterPropertiesSet(): Unit = {
    context.system.scheduler.schedule(5 seconds, 1 seconds, self, new UpdateEvent())
  }

  override def receive: Receive = {
    case event:AddListenerEvent =>
      listeners = event.getActorRef :: listeners
    case event:RemoveListenerEvent =>
      listeners = listeners.filter(_ == event.getActorRef)
    case _:UpdateEvent =>
      if(listeners.nonEmpty) {
        val updateScene: UpdateScene = UpdateScene(getScene)
        listeners.foreach(target => target ! updateScene)
      }
  }

  def getScene: Scene = {
    val blocks: List[Block] = yCamProtocol(BEFORE_FRAME)
    val scene: Scene = new Scene
    scene.setTime(Calendar.getInstance())
    scene.setBlocks(blocks.asJava)
    scene
  }

  @tailrec private def yCamProtocol(state: VisionActorState, blocks: List[Block] = null): List[Block] = {
    state match {
      case BEFORE_FRAME =>
        while (visionAdapter.readByte != 0xAA.toByte) {}
        yCamProtocol(FIRST_AA_BYTE_READ, blocks)
      case FIRST_AA_BYTE_READ =>
        if (visionAdapter.readByte == 0x55.toByte)
          yCamProtocol(FIRST_55_BYTE_READ, blocks)
        else
          yCamProtocol(BEFORE_FRAME, blocks)
      case FIRST_55_BYTE_READ =>
        val word: Int = visionAdapter.readWord
        if (word == 0xAA55)
          yCamProtocol(NORMAL_BLOCK_SYNC_WORD_READ, blocks)
        else if (word == 0xAA56)
          yCamProtocol(COLOR_BLOCK_SYNC_WORD_READ, blocks)
        else
          blocks
      case NORMAL_BLOCK_SYNC_WORD_READ =>
        val checksum = visionAdapter.readWord
        val block = new Block
        block.setSignature(visionAdapter.readWord)
        block.setX(visionAdapter.readWord)
        block.setY(visionAdapter.readWord)
        block.setWidth(visionAdapter.readWord)
        block.setHeight(visionAdapter.readWord)
        if (block.getChecksum == checksum) {
          if(blocks == null) {
            yCamProtocol(FIRST_55_BYTE_READ, List[Block](block))
          } else {
            yCamProtocol(FIRST_55_BYTE_READ, block :: blocks)
          }
        } else {
          yCamProtocol(FIRST_55_BYTE_READ, blocks)
        }
      case COLOR_BLOCK_SYNC_WORD_READ =>
        val checksum = visionAdapter.readWord
        val colorBlock = new ColorBlock
        colorBlock.setSignature(visionAdapter.readWord)
        colorBlock.setX(visionAdapter.readWord)
        colorBlock.setY(visionAdapter.readWord)
        colorBlock.setWidth(visionAdapter.readWord)
        colorBlock.setHeight(visionAdapter.readWord)
        colorBlock.setAngle(visionAdapter.readWord)
        if (colorBlock.getChecksum == checksum) {
          if(blocks == null) {
            yCamProtocol(FIRST_55_BYTE_READ, List[Block](colorBlock))
          } else {
            yCamProtocol(FIRST_55_BYTE_READ, colorBlock :: blocks)
          }
        } else {
          yCamProtocol(FIRST_55_BYTE_READ, blocks)
        }
    }
  }

  sealed abstract class VisionActorState
  case object BEFORE_FRAME extends VisionActorState
  case object FIRST_AA_BYTE_READ extends VisionActorState
  case object FIRST_55_BYTE_READ extends VisionActorState
  case object NORMAL_BLOCK_SYNC_WORD_READ extends VisionActorState
  case object COLOR_BLOCK_SYNC_WORD_READ extends VisionActorState
}

object VisionActor {
  case class UpdateScene(scene:Scene) {}
}
