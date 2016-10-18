package de.codecentric.iot.rapiro.blazeds.destinations;

import de.codecentric.iot.rapiro.blazeds.actors.BlazeDsPublishingActor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 18.10.16.
 */
@Scope("prototype")
@Component("movementPublishingActor")
public class MovementPublishingActor extends BlazeDsPublishingActor {

    public MovementPublishingActor() {
        super("movementEvents");
    }

}
