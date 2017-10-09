package de.codecentric.iot.rapiro.movement.actors

import java.util.Calendar

import akka.actor.{Actor, ActorRef}
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent}
import de.codecentric.iot.rapiro.movement.adapter.{AsyncReader, SerialAdapter}
import de.codecentric.iot.rapiro.movement.model.MovementState
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import com.typesafe.scalalogging._
import de.codecentric.iot.rapiro.akka.ItemEvent
import de.codecentric.iot.rapiro.movement.actors.MovementActor.UpdateMovementState

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope("prototype")
@Component("movementActor")
class MovementActor extends Actor with LazyLogging with InitializingBean with AsyncReader {

  @Autowired private val serialAdapter:SerialAdapter = null

  val SIZE_ARDUINO_FRAME:Int = 2 /* Header */ + 24 /* Servo positions */ + 6 /* Eye colors */ + 2 /* IR Distance */

  var listeners: List[ActorRef] = List[ActorRef]()

  var curMovementState:MovementState = _

  override def afterPropertiesSet(): Unit = {
    serialAdapter.addAsyncReader(this)
    logger.info("Scheduled MovementActor")
  }

  override def receive: Receive = {
    case event:AddListenerEvent =>
      listeners = event.getActorRef :: listeners
    case event:RemoveListenerEvent =>
      listeners = listeners.filter(_ == event.getActorRef)
  }

  override def dataAvailable(): Unit = {
    // Read in all the data till the first 0xFF is read.
    while(serialAdapter.peekByte() != 0xFF && serialAdapter.peekByte() != -1) {
      serialAdapter.readByte()
    }
    if(serialAdapter.bytesAvailable() >= SIZE_ARDUINO_FRAME) {
      // The header of a Arduino frame is two 0xFF bytes.
      if(serialAdapter.readByte() == 0xFF) {
        if(serialAdapter.readByte() == 0xFF) {
          // After that come the 12 positions of the servos
          val positions: Array[Int] = new Array[Int](12)
          for(i <- 0 to 11) {
            val servoPosition:Int = serialAdapter.readWord
            positions(i) = servoPosition
          }
          // After that come the 3 colors of the eyes
          val eyeColors: Array[Int] = new Array[Int](3)
          for(i <- 0 to 2) {
            val eyeColor:Int = serialAdapter.readWord
            eyeColors(i) = eyeColor
          }
          // Finally the distance the distance sensor is producing
          val irDistance:Int = serialAdapter.readWord

          if (listeners.nonEmpty) {
            // Assemble a new movement state object
            val movementState: MovementState = new MovementState()
            movementState.setTime(Calendar.getInstance())
            movementState.setServoPositions(positions)
            movementState.setEyeColors(eyeColors)
            movementState.setIrDistance(irDistance)

            val updateMovementState: UpdateMovementState = UpdateMovementState(movementState)
            listeners.foreach(target => target ! updateMovementState)
          }

        }
      }
    }
  }
}

object MovementActor {
  case class UpdateMovementState(movementState: MovementState) extends ItemEvent[MovementState] {
    override def getItem: MovementState = {
      movementState
    }
  }
}

