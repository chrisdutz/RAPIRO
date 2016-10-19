package de.codecentric.iot.rapiro.akka.events;

import akka.actor.ActorRef;

/**
 * Created by christoferdutz on 19.10.16.
 */
public class AddListenerEvent {

    private ActorRef actorRef;

    public AddListenerEvent(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

}
