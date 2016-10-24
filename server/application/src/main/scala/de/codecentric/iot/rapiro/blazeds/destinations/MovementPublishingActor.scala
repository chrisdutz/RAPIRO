package de.codecentric.iot.rapiro.blazeds.destinations

import de.codecentric.iot.rapiro.blazeds.actors.BlazeDsPublishingActor
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
  * Created by christoferdutz on 24.10.16.
  */
@Scope ("prototype")
@Component ("movementPublishingActor")
class MovementPublishingActor extends BlazeDsPublishingActor {

  override def destinationName: String = "movementEvents"

}
