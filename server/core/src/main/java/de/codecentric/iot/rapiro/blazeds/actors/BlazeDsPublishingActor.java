package de.codecentric.iot.rapiro.blazeds.actors;

import akka.japi.pf.ReceiveBuilder;
import akka.stream.actor.AbstractActorSubscriber;
import akka.stream.actor.ActorSubscriberMessage;
import akka.stream.actor.OneByOneRequestStrategy;
import akka.stream.actor.RequestStrategy;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.messaging.MessageTemplate;

/**
 * Created by christoferdutz on 18.10.16.
 */
public abstract class BlazeDsPublishingActor extends AbstractActorSubscriber implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(BlazeDsPublishingActor.class);

    @Autowired
    private MessageBroker broker;

    @Autowired
    private MessageTemplate template;

    private String destinationName;

    public BlazeDsPublishingActor(String destinationName) {
        this.destinationName = destinationName;
        receive(ReceiveBuilder.
                match(ActorSubscriberMessage.OnNext.class, on -> on.element() instanceof Object,
                        onNext -> {
                            Object object = onNext.element();
                            LOG.debug("Sending " + object + " to destination " + destinationName);
                            template.send(destinationName, object);
                        }).
                build());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        flex.messaging.services.Service service = broker.getService("messaging-service");
        Destination destination = service.createDestination(destinationName);
        service.addDestination(destination);
        if(!service.isStarted()) {
            service.start();
        } else if(!destination.isStarted()) {
            destination.start();
        }
    }

    @Override
    public RequestStrategy requestStrategy() {
        return OneByOneRequestStrategy.getInstance();
    }

}
