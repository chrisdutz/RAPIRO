package de.codecentric.iot.rapiro.akka.events;

import akka.actor.ActorRef;

/**
 * Created by christoferdutz on 19.10.16.
 */
public class RemoveListenerEvent {

    private ActorRef actorRef;

    public RemoveListenerEvent(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

}
