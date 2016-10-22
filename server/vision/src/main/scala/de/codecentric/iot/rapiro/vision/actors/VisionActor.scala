package de.codecentric.iot.rapiro.vision.actors

import java.util.Calendar

import akka.actor.{Actor, ActorRef}
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent, UpdateEvent}
import de.codecentric.iot.rapiro.vision.actors.VisionActor.UpdateScene
import de.codecentric.iot.rapiro.vision.model.{Block, ColorBlock, Scene}
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.concurrent.duration.DurationInt
import com.typesafe.scalalogging._
import de.codecentric.iot.rapiro.vision.adapter.SpiAdapter

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope ("prototype")
@Component ("visionActor")
class VisionActor() extends Actor with InitializingBean with LazyLogging {
  import context.dispatcher

  @Autowired private val spiAdapter: SpiAdapter = null

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
        val scene:Scene = getScene
        val updateScene: UpdateScene = UpdateScene(scene)
        listeners.foreach(target => target ! updateScene)
      }
  }

  def getScene: Scene = {
    val blocks: List[Block] = yCamProtocol(BEFORE_FRAME)
    val scene: Scene = new Scene
    scene.setTime(Calendar.getInstance())
    val javaBlocks: java.util.List[Block] = seqAsJavaList(blocks)
    scene.setBlocks(javaBlocks)
    scene
  }

  @tailrec private def yCamProtocol(state: VisionActorState, blocks: List[Block] = null): List[Block] = {
    state match {
      case BEFORE_FRAME =>
        var curByte: Byte = 0
        var bytesRead: Int = 0
        // Start reading bytes until we read the start byte.
        do {
          curByte = spiAdapter.readByte
          bytesRead += 1
        } while ((curByte != 0xAA.toByte) && (bytesRead < 100))

        // Continue if we read the start byte.
        if(curByte == 0xAA.toByte) {
          yCamProtocol(FIRST_AA_BYTE_READ, blocks)
        }
        // Abort if the start byte wasn't read after at least 100 bytes.
        else {
          logger.debug("Giving up trying to find start of frame.")
          null
        }
      case FIRST_AA_BYTE_READ =>
        if (spiAdapter.readByte == 0x55.toByte)
          yCamProtocol(FIRST_55_BYTE_READ, blocks)
        else
          yCamProtocol(BEFORE_FRAME, blocks)
      case FIRST_55_BYTE_READ =>
        val word: Int = spiAdapter.readWord
        if (word == 0xAA55)
          yCamProtocol(NORMAL_BLOCK_SYNC_WORD_READ, blocks)
        else if (word == 0xAA56)
          yCamProtocol(COLOR_BLOCK_SYNC_WORD_READ, blocks)
        else {
          if(blocks != null) {
            logger.trace("Exiting with " + blocks.length + " blocks")
          } else {
            logger.trace("Exiting without blocks")
          }
          blocks
        }
      case NORMAL_BLOCK_SYNC_WORD_READ =>
        val checksum = spiAdapter.readWord
        val block = new Block
        block.setSignature(spiAdapter.readWord)
        block.setX(spiAdapter.readWord)
        block.setY(spiAdapter.readWord)
        block.setWidth(spiAdapter.readWord)
        block.setHeight(spiAdapter.readWord)
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
        val checksum = spiAdapter.readWord
        val colorBlock = new ColorBlock
        colorBlock.setSignature(spiAdapter.readWord)
        colorBlock.setX(spiAdapter.readWord)
        colorBlock.setY(spiAdapter.readWord)
        colorBlock.setWidth(spiAdapter.readWord)
        colorBlock.setHeight(spiAdapter.readWord)
        colorBlock.setAngle(spiAdapter.readWord)
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
