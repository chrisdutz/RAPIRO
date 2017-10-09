package de.codecentric.iot.rapiro.movement;

import de.codecentric.iot.rapiro.movement.adapter.SerialAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

/**
 * Created by christoferdutz on 23.03.16.
 */
@Service("movementService")
@RemotingDestination
public class MovementService implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(MovementService.class);

    @Autowired
    private SerialAdapter serialAdapter;

    public void stop() {
        LOG.info("Movement: Stop");
        serialAdapter.send("#M0");
    }

    public void moveForward() {
        LOG.info("Movement: Forward");
        serialAdapter.send("#M1");
    }

    public void moveLeft() {
        LOG.info("Movement: Left");
        serialAdapter.send("#M4");
    }

    public void moveRight() {
        LOG.info("Movement: Right");
        serialAdapter.send("#M3");
    }

    public void moveBack() {
        LOG.info("Movement: Back");
        serialAdapter.send("#M2");
    }

    public void setEyeColor(int red, int green, int blue) {
        LOG.info("Movement: Eye Color");
        serialAdapter.send("#PR" + String.format("%03d", red) + "G" + String.format("%03d", green) + "B" + String.format("%03d", blue) + "T010");
    }

    public void turnHead(int value) {
        serialAdapter.send("#PS00A" + String.format("%03d", value) + "T010");
    }

    public void powerDown() {
        LOG.info("Movement: Power Down");
        serialAdapter.send("#H0");
    }

    /**
     * Turn the servos off to avoid having the little chap shiver all the time.
     * @param applicationEvent the spring {@link ContextRefreshedEvent} instance
     */
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        // Initialize all servos to default positions.
        serialAdapter.send("#M0");
        // Give Rapiro some time to position.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore.
        }
        // Power down all servos.
        powerDown();
    }

}
