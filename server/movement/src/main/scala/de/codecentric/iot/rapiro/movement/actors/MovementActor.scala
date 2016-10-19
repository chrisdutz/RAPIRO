package de.codecentric.iot.rapiro.movement.actors

import java.util.Calendar

import akka.actor.{Actor, ActorRef}
import de.codecentric.iot.rapiro.akka.events.{AddListenerEvent, RemoveListenerEvent, UpdateEvent}
import de.codecentric.iot.rapiro.movement.actors.MovementActor.UpdatePosition
import de.codecentric.iot.rapiro.movement.adapter.SerialAdapter
import de.codecentric.iot.rapiro.movement.model.Position
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.duration.DurationInt

/**
  * Created by christoferdutz on 19.10.16.
  */
@Scope("prototype")
@Component("movementActor")
class MovementActor extends Actor with InitializingBean {
  import context.dispatcher

  private val RESPONSE_LENGTH = 10

  @Autowired private val serialAdapter:SerialAdapter = null

  var listeners: List[ActorRef] = List[ActorRef]()

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
        val updateScene: UpdatePosition = UpdatePosition(getPosition)
        listeners.foreach(target => target ! updateScene)
      }
  }

  def getPosition: Position = {
    // Read the state from the Arduino (or simulate in simulation mode).
    var readString:String = null
    serialAdapter.send("#S")
    readString = serialAdapter.read(RESPONSE_LENGTH)
    // Parse the response.
    val segments = readString.substring(2).split(":")
    val servoPositionStrings = segments(0)
    val eyeColorStrings = segments(1)
    val irSensorString = segments(2).trim
    // Convert the string values into numeric ones.
    val numServos = servoPositionStrings.length / 3
    val servoPositions = new Array[Int](numServos)
    for(a <- 0 to 12) {
      servoPositions(a) = Integer.valueOf(servoPositionStrings.substring(a * 3, (a + 1) * 3))
    }
    val numEyeColors = eyeColorStrings.length / 6
    val eyeColors = new Array[Int](numEyeColors)
    var i = 0
    for(a <- 0 to 3) {
      eyeColors(i) = Integer.valueOf(eyeColorStrings.substring(i * 6, (i + 1) * 6))
    }
    val irSensor = Integer.valueOf(irSensorString)
    // Set the values in the position element.
    val position = new Position
    position.setTime(Calendar.getInstance())
    position.setServoPositions(servoPositions)
    position.setEyeColors(eyeColors)
    position.setIrDistance(irSensor)
    position
  }

}

object MovementActor {
  case class UpdatePosition(position: Position) {}
}

