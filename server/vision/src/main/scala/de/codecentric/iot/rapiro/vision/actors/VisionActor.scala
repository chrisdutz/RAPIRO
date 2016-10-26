package de.codecentric.iot.rapiro.vision.actors

import java.util.Calendar

import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging._
import de.codecentric.iot.rapiro.akka.ItemEvent
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent, UpdateEvent}
import de.codecentric.iot.rapiro.vision.actors.VisionActor.UpdateScene
import de.codecentric.iot.rapiro.vision.adapter.SpiAdapter
import de.codecentric.iot.rapiro.vision.model.{Block, ColorBlock, Scene}
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.annotation.tailrec
import scala.concurrent.duration.DurationInt

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope ("prototype")
@Component ("visionActor")
class VisionActor() extends Actor with LazyLogging with InitializingBean {
  import context.dispatcher

  @Autowired private val spiAdapter: SpiAdapter = null

  var running: Boolean = false

  var listeners:List[ActorRef] = List[ActorRef]()

  override def afterPropertiesSet(): Unit = {
    context.system.scheduler.schedule(5 seconds, 100 millis, self, new UpdateEvent())
    logger.info("Scheduled VisionActor")
  }

  override def receive: Receive = {
    case event:AddListenerEvent =>
      listeners = event.getActorRef :: listeners
    case event:RemoveListenerEvent =>
      listeners = listeners.filter(_ == event.getActorRef)
    case _:UpdateEvent =>
      if(!running) {
        try {
          running = true
          if (listeners.nonEmpty) {
            val scene: Scene = getScene
            val updateScene: UpdateScene = UpdateScene(scene)
            listeners.foreach(target => target ! updateScene)
          }
        } catch {
          case e: Exception => logger.error("An error occurred while processing incoming update event.", e)
        } finally {
          running = false
        }
      }
  }

  def getScene: Scene = {
    val blocks: Array[Block] = yCamProtocol(BEFORE_FRAME)
    val scene: Scene = new Scene
    scene.setTime(Calendar.getInstance())
    scene.setBlocks(blocks)
    scene
  }

  @tailrec private def yCamProtocol(state: VisionActorState, blocks: Array[Block] = null): Array[Block] = {
    state match {
      case BEFORE_FRAME =>
        var curByte: Byte = 0
        var bytesRead: Int = 0
        // Start reading bytes until we read the start byte.
        do {
          curByte = spiAdapter.readByte
          bytesRead += 1
        } while (curByte != 0xAA.toByte)

        // Continue if we read the start byte.
        if(curByte == 0xAA.toByte) {
          logger.info("Read block start after {} empty chars", bytesRead)
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
        if ((block.getSurfaceSize > 100) && (block.getChecksum == checksum)) {
          if(blocks == null) {
            yCamProtocol(FIRST_55_BYTE_READ, Array[Block](block))
          } else {
            yCamProtocol(FIRST_55_BYTE_READ, blocks :+ block)
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
            yCamProtocol(FIRST_55_BYTE_READ, Array[Block](colorBlock))
          } else {
            yCamProtocol(FIRST_55_BYTE_READ, blocks :+ colorBlock)
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
  case class UpdateScene(scene:Scene) extends ItemEvent[Scene] {
    override def getItem: Scene = scene
  }
}
