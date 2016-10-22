package de.codecentric.iot.rapiro.movement;

import de.codecentric.iot.rapiro.akka.AkkaProducerConfig;
import de.codecentric.iot.rapiro.movement.model.MovementState;
import org.springframework.context.annotation.Configuration;

/**
 * Created by christoferdutz on 13.09.16.
 */
@Configuration
public class MovementConfig extends AkkaProducerConfig<MovementState> {

    public MovementConfig() {
        super(MovementState.class);
    }

    @Override
    protected String getActorName() {
        return "positionActor";
    }

}
