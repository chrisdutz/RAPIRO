package de.codecentric.iot.rapiro.responses

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import de.codecentric.iot.rapiro.movement.MovementService
import de.codecentric.iot.rapiro.movement.actors.MovementActor.UpdateMovementState
import de.codecentric.iot.rapiro.movement.model.MovementState
import de.codecentric.iot.rapiro.vision.actors.VisionActor.UpdateScene
import de.codecentric.iot.rapiro.vision.model.Block
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
  * Created by christoferdutz on 24.10.16.
  */
@Scope("prototype")
@Component("lookAtOmNomResponse")
class LookAtOmNomResponse extends Actor with LazyLogging {

  @Autowired var movementService: MovementService = _

  private val cameraCenter: Int = 180

  var movementState: MovementState = _

  override def receive: Receive = {
    case event: UpdateScene =>
      logger.debug("Got scene with width {}", event.scene.getWidth)
      if (event.scene.getWidth > 0 && event.scene.getBlocks != null && event.scene.getBlocks.length > 0) {
        var biggestBlock: Block = null
        event.scene.getBlocks.foreach(block => {
          logger.debug("Got block with size {}", block.getSurfaceSize)
          if ((biggestBlock == null) ||
            (biggestBlock.getSurfaceSize < block.getSurfaceSize)) {
            biggestBlock = block
          }
        })
        logger.debug("Biggest block with size: {}", biggestBlock)

        // Get the x coordinate of the scene center.
        val sceneCenter: Int = event.scene.getWidth / 2
        logger.debug("Scene Center: {}", sceneCenter)

        // Calculate the x position of the object relative to the scene center.
        val absoluteObjectPositionRelativeToCenter: Int = (biggestBlock.getX + (biggestBlock.getWidth / 2)) - sceneCenter
        logger.debug("Absolute Object Position Relative To Center: {}", absoluteObjectPositionRelativeToCenter)

        // Calculate how many percent the object is relative to the scene center.
        val relativeObjectPositionRelativeToCenter: Int = absoluteObjectPositionRelativeToCenter / (event.scene.getWidth / 200)
        logger.debug("Relative Object Position Relative To Center: {}", relativeObjectPositionRelativeToCenter)

        // Calculate the movement based upon the current location and the detected position.
        val headPosition: Int = calculateNewPosition(relativeObjectPositionRelativeToCenter)
        logger.debug("Calculated new Head Position: {}", headPosition)

        // Make sure the position is not lower than 0° and higher than 180°
        val safePosition: Int = Math.max(0, Math.min(180, headPosition))
        logger.debug("Calculated new Safe Head Position: {}", safePosition)

        movementService.turnHead(safePosition)
        logger.debug("Turning head to: {}", headPosition)
        //} else {
        //movementService.turnHead(90)
        //logger.debug("Turning head to default position: 90")
      }
    case event: UpdateMovementState =>
      movementState = event.movementState
  }

  def calculateNewPosition(objectPositionInPercent: Int): Int = {
    // Camera position 0 = looking right, 180 = looking left, 90 = looking straight ahead.

    // The camera will produce values between 0 and 319 for x and 0 to 199 for y values.
    /*if(movementState != null) {
      val currentHeadPosition = movementState.getServoPositions()(0)
      logger.debug("Current Head Position: " + currentHeadPosition)
      // Translate the top-left coordinate to a center coordinate.
      val relObjPos: Int = objectPosition - cameraCenter
      // The range goes from 0 to 319 which is greater than the 0 to 180 of the servo
      // And the range of 0 to 319 is the view range which is greatly smaller than the
      // 180° of the movement range.
      val turn:Int = relObjPos / 6
      val nextHeadPosition =  currentHeadPosition - turn
      logger.trace("Next head position: {}", nextHeadPosition)
      nextHeadPosition
    }
    // As default, just turn the head to the center position.
    else {*/
    90
    //}
  }

}
