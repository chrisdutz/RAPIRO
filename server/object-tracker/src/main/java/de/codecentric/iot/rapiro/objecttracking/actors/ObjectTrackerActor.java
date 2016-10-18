package de.codecentric.iot.rapiro.objecttracking.actors;

import akka.japi.pf.ReceiveBuilder;
import akka.stream.actor.AbstractActorSubscriber;
import akka.stream.actor.ActorSubscriberMessage;
import akka.stream.actor.MaxInFlightRequestStrategy;
import akka.stream.actor.RequestStrategy;
import de.codecentric.iot.rapiro.vision.model.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by christoferdutz on 14.10.16.
 */
@Scope("prototype")
@Component("objectTrackerActor")
public class ObjectTrackerActor extends AbstractActorSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectTrackerActor.class);

    final int MAX_QUEUE_SIZE = 10;
    final List<Scene> queue = new LinkedList<>();

    public ObjectTrackerActor() {
        receive(ReceiveBuilder.
                match(ActorSubscriberMessage.OnNext.class, on -> on.element() instanceof Scene,
                        onNext -> {
                            Scene scene = (Scene) onNext.element();
                            queue.add(scene);
                            System.out.println(scene);
                        }).
                build());
    }


    @Override
    public RequestStrategy requestStrategy() {
        return new MaxInFlightRequestStrategy(MAX_QUEUE_SIZE) {
            @Override
            public int inFlightInternally() {
                return queue.size();
            }
        };
    }
}
