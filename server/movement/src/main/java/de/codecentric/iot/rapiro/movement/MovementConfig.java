package de.codecentric.iot.rapiro.movement;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import de.codecentric.iot.rapiro.akka.SpringExtension;
import de.codecentric.iot.rapiro.movement.model.PositionProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import scala.concurrent.duration.FiniteDuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Created by christoferdutz on 13.09.16.
 */
@Configuration
public class MovementConfig {

    @Autowired
    private ActorSystem actorSystem;

    @PostConstruct
    public void initAkka() {
/*        ActorRef actor = actorSystem.actorOf(
                SpringExtension.SpringExtProvider.get(actorSystem).props("movementActor"), "counter");

        FiniteDuration duration = FiniteDuration.create(100, TimeUnit.MILLISECONDS);
        actorSystem.scheduler().schedule(duration, duration,
                actor, new PositionProtocol(),
                actorSystem.dispatcher(), null);*/
    }

}
