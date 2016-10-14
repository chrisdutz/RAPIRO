package de.codecentric.iot.rapiro.movement.actors;

import de.codecentric.iot.rapiro.akka.actors.AbstractActor;
import de.codecentric.iot.rapiro.movement.adapter.SerialAdapter;
import de.codecentric.iot.rapiro.movement.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by christoferdutz on 12.09.16.
 */
@Component("positionActor")
@Scope("prototype")
public class PositionActor extends AbstractActor<Position> {

    private static final Logger LOG = LoggerFactory.getLogger(PositionActor.class);

    private static final int RESPONSE_LENGTH = 10;

    @Autowired
    private SerialAdapter serialAdapter;

    @Override
    public void onNext(Position element) {
        super.onNext(element);
    }

    protected List<Position> getItems() {
        try {
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
            Position position = new Position();
            position.setTimestamp(new Date().getTime());
            position.setServoPositions(servoPositions);
            position.setEyeColors(eyeColors);
            position.setIrDistance(irSensor);
            return Collections.singletonList(position);
        } catch (Exception e) {
            LOG.warn("An error occurred in getItems()", e);
            throw e;
        }
    }

}
