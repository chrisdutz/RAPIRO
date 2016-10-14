package de.codecentric.iot.rapiro.akka.actors;

import akka.japi.pf.ReceiveBuilder;
import akka.stream.actor.AbstractActorPublisher;
import akka.stream.actor.ActorPublisherMessage;
import de.codecentric.iot.rapiro.akka.events.UpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by christoferdutz on 14.10.16.
 */
public abstract class AbstractActor<T> extends AbstractActorPublisher<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractActor.class);

    private static final int MAX_BUFFER_SIZE = 100;

    private final List<T> buf = new ArrayList<>();


    public AbstractActor() {
        // ---------------------------------------------------------------
        // Initialize the akka stuff.
        // ---------------------------------------------------------------

        // Define what the actor should do every time it's triggered.
        receive(ReceiveBuilder.
                // If a position request is coming in and the number of requests in
                // the queue exceeds the buffer size, return with an error response.
                match(UpdateEvent.class, updateRequest -> buf.size() == MAX_BUFFER_SIZE, updateRequest -> {
                    // TODO: In this case the buffer is full ...
                    // doesn't make sense to send the scheduler a response as it doesn't know what to do with it.
                    LOG.info("Buffer full");
                }).
                // If a position request is coming in and the queue is not full,
                // respond with an acknowledge response.
                match(UpdateEvent.class, job -> {
                    List<T> items = getItems();
                    for(T item : items) {
                        // If the buffer is empty, respond immediately.
                        if (buf.isEmpty() && totalDemand() > 0) {
                            onNext(item);
                        }
                        // If the buffer is not empty, add the current response to the queue and
                        // try to start delivering the buffer.
                        else {
                            buf.add(item);
                            deliverBuf();
                        }
                    }
                }).
                match(ActorPublisherMessage.Request.class, request -> deliverBuf()).
                match(ActorPublisherMessage.Cancel.class, cancel -> context().stop(self())).
                build());

        // Schedule the updating of position data every 100ms
        // TODO: This is rather ugly ... find a way to start this from outside the initialization.
        FiniteDuration duration = FiniteDuration.create(1000, TimeUnit.MILLISECONDS);
        context().system().scheduler().schedule(duration, duration,
                self(), new UpdateEvent(),
                context().system().dispatcher(), null);
    }

    private void deliverBuf() {
        while (totalDemand() > 0) {
            // Get max "totalDemand" number of elements from the queue and send them back.
            if (totalDemand() <= Integer.MAX_VALUE) {
                final List<T> took =
                        buf.subList(0, Math.min(buf.size(), (int) totalDemand()));
                took.forEach(this::onNext);
                buf.removeAll(took);
                break;
            }
            // If "totalDemand" was greater than the maximum int value, just return
            // max-int results and have the next batch delivered next time.
            else {
                final List<T> took =
                        buf.subList(0, Math.min(buf.size(), Integer.MAX_VALUE));
                took.forEach(this::onNext);
                buf.removeAll(took);
            }
        }
    }

    protected abstract List<T> getItems();
}
