package de.codecentric.iot.rapiro.blazeds.destinations;

import de.codecentric.iot.rapiro.blazeds.actors.BlazeDsPublishingActor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 18.10.16.
 */
@Scope("prototype")
@Component("telemetryPublishingActor")
public class TelemetryPublishingActor extends BlazeDsPublishingActor {

    public TelemetryPublishingActor() {
        super("telemetryEvents");
    }

}
