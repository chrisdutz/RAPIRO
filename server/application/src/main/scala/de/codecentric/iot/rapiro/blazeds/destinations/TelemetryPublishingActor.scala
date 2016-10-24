package de.codecentric.iot.rapiro.blazeds.destinations

import de.codecentric.iot.rapiro.blazeds.actors.BlazeDsPublishingActor
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
  * Created by christoferdutz on 24.10.16.
  */
@Scope ("prototype")
@Component ("telemetryPublishingActor")
class TelemetryPublishingActor extends BlazeDsPublishingActor {

  override def destinationName: String = "telemetryEvents"

}
