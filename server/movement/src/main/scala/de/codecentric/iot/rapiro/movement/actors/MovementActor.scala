package de.codecentric.iot.rapiro.movement.actors

import java.util.Calendar

import akka.actor.{Actor, ActorRef}
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent, UpdateEvent}
import de.codecentric.iot.rapiro.movement.actors.MovementActor.UpdateMovementState
import de.codecentric.iot.rapiro.movement.adapter.SerialAdapter
import de.codecentric.iot.rapiro.movement.model.MovementState
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.annotation.tailrec
import scala.concurrent.duration.DurationInt
import com.typesafe.scalalogging._
import de.codecentric.iot.rapiro.akka.ItemEvent

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope("prototype")
@Component("movementActor")
class MovementActor extends Actor with LazyLogging with InitializingBean {
  import context.dispatcher

  @Autowired private val serialAdapter:SerialAdapter = null

  var listeners: List[ActorRef] = List[ActorRef]()

  override def afterPropertiesSet(): Unit = {
    context.system.scheduler.schedule(5 seconds, 1 seconds, self, new UpdateEvent())
    logger.info("Scheduled MovementActor")
  }

  override def receive: Receive = {
    case event:AddListenerEvent =>
      listeners = event.getActorRef :: listeners
    case event:RemoveListenerEvent =>
      listeners = listeners.filter(_ == event.getActorRef)
    case _:UpdateEvent =>
      try {
        if (listeners.nonEmpty) {
          val updateMovementState: UpdateMovementState = UpdateMovementState(getMovementState)
          listeners.foreach(target => target ! updateMovementState)
        }
      } catch {
        case e: Exception => logger.error("An error occurred while processing incoming update event.", e)
      }
  }

  def getMovementState: MovementState = {
    arduinoProtocol(BEFORE_FRAME)
  }

  @tailrec private def arduinoProtocol(state: MovementActorState): MovementState = {
    state match {
      case BEFORE_FRAME =>
        var curByte: Byte = 0
        // Start reading bytes until we read the start byte.
        do {
          curByte = serialAdapter.readByte
        } while (curByte != 0xFF.toByte)

        // Continue if we read the start byte.
        if(curByte == 0xFF.toByte) {
          arduinoProtocol(FIRST_FF_BYTE_READ)
        }
        // Abort if the start byte wasn't read after at least 100 bytes.
        else {
          logger.debug("Giving up trying to find start of frame.")
          null
        }
      case FIRST_FF_BYTE_READ =>
        if (serialAdapter.readByte == 0xFF.toByte)
          arduinoProtocol(HEADER_READ)
        else
          arduinoProtocol(BEFORE_FRAME)
      case HEADER_READ =>
        val positions: Array[Int] = new Array[Int](12)
        for(i <- 0 to 11) {
          val servoPosition:Int = serialAdapter.readWord
          positions(i) = servoPosition
        }
        val eyeColors: Array[Int] = new Array[Int](3)
        for(i <- 0 to 2) {
          val eyeColor:Int = serialAdapter.readWord
          eyeColors(i) = eyeColor
        }
        val irDistance:Int = serialAdapter.readWord

        val movementState: MovementState = new MovementState()
        movementState.setTime(Calendar.getInstance())
        movementState.setServoPositions(positions)
        movementState.setEyeColors(eyeColors)
        movementState.setIrDistance(irDistance)
        movementState
    }
  }


  sealed abstract class MovementActorState
  case object BEFORE_FRAME extends MovementActorState
  case object FIRST_FF_BYTE_READ extends MovementActorState
  case object HEADER_READ extends MovementActorState

}

object MovementActor {
  case class UpdateMovementState(movementState: MovementState) extends ItemEvent[MovementState] {
    override def getItem: MovementState = {
      movementState
    }
  }
}

