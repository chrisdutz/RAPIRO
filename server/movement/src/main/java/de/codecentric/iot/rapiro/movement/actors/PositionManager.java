package de.codecentric.iot.rapiro.movement.actors;

import akka.japi.pf.ReceiveBuilder;
import akka.stream.actor.AbstractActorPublisher;
import akka.stream.actor.ActorPublisherMessage;
import de.codecentric.iot.rapiro.movement.adapter.SerialAdapter;
import de.codecentric.iot.rapiro.movement.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by christoferdutz on 12.09.16.
 */
@Component("movementManager")
@Scope("prototype")
public class PositionManager extends AbstractActorPublisher<Position> {

    private static final Logger LOG = LoggerFactory.getLogger(PositionManager.class);

    private static final int RESPONSE_LENGTH = 10;
    private static final int MAX_BUFFER_SIZE = 100;

    @Autowired
    private SerialAdapter serialAdapter;

    private final List<Position> buf = new ArrayList<>();

    public PositionManager() {
        // ---------------------------------------------------------------
        // Initialize the akka stuff.
        // ---------------------------------------------------------------

        // Define what the actor should do every time it's triggered.
        receive(ReceiveBuilder.
                // If a position request is coming in and the number of requests in
                // the queue exceeds the buffer size, return with an error response.
                match(Position.class, positionRequest -> buf.size() == MAX_BUFFER_SIZE, positionRequest -> {
                    // TODO: In this case the buffer is full ...
                    // doesn't make sense to send the scheduler a response as it doesn't know what to do with it.
                    // sender().tell(PositionProtocol.UpdatePositionDenied, self());
                    LOG.info("Buffer full");
                }).
                // If a position request is coming in and the queue is not full,
                // respond with an acknowledge response.
                match(Position.class, job -> {
                    //sender().tell(PositionProtocol.UpdatePositionAccepted, self());

                    // If the buffer is empty, respond immediately.
                    if (buf.isEmpty() && totalDemand() > 0)
                        onNext(job);
                    // If the buffer is not empty, add the current response to the queue and
                    // try to start delivering the buffer.
                    else {
                        buf.add(job);
                        deliverBuf();
                    }
                }).
                match(ActorPublisherMessage.Request.class, request -> deliverBuf()).
                match(ActorPublisherMessage.Cancel.class, cancel -> context().stop(self())).
                build());

        // Schedule the updating of position data every 100ms
        // TODO: This is rather ugly ... find a way to start this from outside the initialization.
        FiniteDuration duration = FiniteDuration.create(100, TimeUnit.MILLISECONDS);
        context().system().scheduler().schedule(duration, duration,
                self(), new Position(),
                context().system().dispatcher(), null);
    }

    private void deliverBuf() {
        while (totalDemand() > 0) {
            // Get max "totalDemand" number of elements from the queue and send them back.
            if (totalDemand() <= Integer.MAX_VALUE) {
                final List<Position> took =
                        buf.subList(0, Math.min(buf.size(), (int) totalDemand()));
                took.forEach(this::onNext);
                buf.removeAll(took);
                break;
            }
            // If "totalDemand" was greater than the maximum int value, just return
            // max-int results and have the next batch delivered next time.
            else {
                final List<Position> took =
                        buf.subList(0, Math.min(buf.size(), Integer.MAX_VALUE));
                took.forEach(this::onNext);
                buf.removeAll(took);
            }
        }
    }

    @Override
    public void onNext(Position element) {
        updatePositionData(element);
        super.onNext(element);
    }

    private void updatePositionData(Position element) {
        // Read the state from the Arduino (or simulate in simulation mode).
        String readString;
        LOG.debug("Movement: Update state ...");
        LOG.debug("Movement: sending '#S'");
        serialAdapter.send("#S");
        readString = serialAdapter.read(RESPONSE_LENGTH);
        LOG.debug("Movement: Update state: '" + readString + "'");

        // Parse the response.
        String[] segments = readString.substring(2).split(":");
        String servoPositionStrings = segments[0];
        String eyeColorStrings = segments[1];
        String irSensorString = segments[2].trim();

        // Convert the string values into numeric ones.
        int numServos = servoPositionStrings.length() / 3;
        int[] servoPositions = new int[numServos];
        for(int i = 0; i < numServos; i++) {
            servoPositions[i] = Integer.valueOf(servoPositionStrings.substring((i * 3), ((i + 1) * 3)));
        }
        int numEyeColors = eyeColorStrings.length() / 6;
        int[] eyeColors = new int[numEyeColors];
        for(int i = 0; i < numEyeColors; i++) {
            eyeColors[i] = Integer.valueOf(eyeColorStrings.substring((i * 6), ((i + 1) * 6)));
        }
        int irSensor = Integer.valueOf(irSensorString);

        // Set the values in the position element.
        element.setTimestamp(new Date().getTime());
        element.setServoPositions(servoPositions);
        element.setEyeColors(eyeColors);
        element.setIrDistance(irSensor);
    }

}
