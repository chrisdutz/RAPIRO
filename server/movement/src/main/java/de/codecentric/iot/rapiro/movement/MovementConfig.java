package de.codecentric.iot.rapiro.movement;

import de.codecentric.iot.rapiro.akka.AkkaProducerConfig;
import de.codecentric.iot.rapiro.movement.model.Position;
import org.springframework.context.annotation.Configuration;

/**
 * Created by christoferdutz on 13.09.16.
 */
@Configuration
public class MovementConfig extends AkkaProducerConfig<Position> {

    public MovementConfig() {
        super(Position.class);
    }

    @Override
    protected String getActorName() {
        return "positionActor";
    }

}
