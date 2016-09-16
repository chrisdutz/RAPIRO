package de.codecentric.iot.rapiro.movement;

import akka.actor.ActorSystem;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created by christoferdutz on 13.09.16.
 */
@Configuration
public class MovementConfig implements InitializingBean {

    private static final String SERVICE_DESTINATION = "movementEvents";

    @Autowired
    private MessageBroker broker;

    @Autowired
    private ActorSystem actorSystem;

    /**
     * Initialize all of the parts related to collecting movement
     * data and routing that to the client.
     */
    @Override
    public void afterPropertiesSet() {
        initAkkaActors();
        initBlazeDsDestinations();
        initKafkaStreams();
    }

    /**
     * Initialize the akka actors for collecting movement data.
     */
    private void initAkkaActors() {
/*        ActorRef actor = actorSystem.actorOf(
                SpringExtension.SpringExtProvider.get(actorSystem).props("movementActor"), "counter");

        FiniteDuration duration = FiniteDuration.create(100, TimeUnit.MILLISECONDS);
        actorSystem.scheduler().schedule(duration, duration,
                actor, new PositionProtocol(),
                actorSystem.dispatcher(), null);*/
    }

    /**
     * Initializes the BlazeDS messaging destinations clients can subscribe to.
     */
    private void initBlazeDsDestinations() {
        flex.messaging.services.Service service = broker.getService("messaging-service");
        Destination visionEvents = service.createDestination(SERVICE_DESTINATION);
        service.addDestination(visionEvents);
        service.start();
    }

    /**
     * Initialize the Kafka streams routing the information gathered by the Akka actors
     * to the BlazeDS messaging destinations.
     */
    private void initKafkaStreams() {

    }

}
