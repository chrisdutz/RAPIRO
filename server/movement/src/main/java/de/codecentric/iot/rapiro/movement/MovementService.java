package de.codecentric.iot.rapiro.movement;

import de.codecentric.iot.rapiro.SystemMode;
import de.codecentric.iot.rapiro.movement.model.MovementState;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import mraa.Result;
import mraa.Uart;
import mraa.UartParity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.flex.messaging.MessageTemplate;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by christoferdutz on 23.03.16.
 */
@Service("movementService")
@RemotingDestination
public class MovementService implements ApplicationListener<ContextRefreshedEvent>, InitializingBean {

    private static final String SERVICE_DESTINATION = "movementEvents";

    private static final String DUMMY_RESPONSE = "#S090091004130090180044090094088086094:000000000000032640:000432\n";

    @Autowired
    private MessageBroker broker;

    @Autowired
    private MessageTemplate template;

    private Uart uart;

    private MovementState movementState;

    public MovementService() {
        if(SystemMode.isRealMode()) {
            uart = new Uart("/dev/ttyMFD1");

            if (uart.setBaudRate(57600) != Result.SUCCESS) {
                System.err.println("UART: Error setting baud rate");
            }

            if (uart.setMode(8, UartParity.UART_PARITY_NONE, 1) != Result.SUCCESS) {
                System.err.println("UART: Error setting mode");
            }

            if (uart.setFlowcontrol(false, false) != Result.SUCCESS) {
                System.err.println("UART: Error setting flow control");
            }
            System.out.println("Movement: Running in real mode");
        } else {
            System.out.println("Movement: Running in simulation mode");
        }
    }

    /**
     * Manually create a messaging destination 'visionEvents' for this service.
     */
    @Override
    public void afterPropertiesSet() {
        flex.messaging.services.Service service = broker.getService("messaging-service");
        Destination visionEvents = service.createDestination(SERVICE_DESTINATION);
        service.addDestination(visionEvents);
        service.start();
    }

    public void stop() {
        System.out.println("Movement: Stop");
        if(uart != null) {
            uart.writeStr("#M0");
        }
    }

    public void moveForward() {
        System.out.println("Movement: Forward");
        if(uart != null) {
            uart.writeStr("#M1");
        }
    }

    public void moveLeft() {
        System.out.println("Movement: Left");
        if(uart != null) {
            uart.writeStr("#M4");
        }
    }

    public void moveRight() {
        System.out.println("Movement: Right");
        if(uart != null) {
            uart.writeStr("#M3");
        }
    }

    public void moveBack() {
        System.out.println("Movement: Back");
        if(uart != null) {
            uart.writeStr("#M2");
        }
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public void setMovementState(MovementState movementState) {
        if(movementState != this.movementState) {
            this.movementState = movementState;
            template.send(SERVICE_DESTINATION, movementState);
        }
    }

    /**
     * Update the vision data
     */
    //@Scheduled(fixedRate = 100)
    public void updateMotionData() {
        // Read the state from the arduino (or simulate in simulation mode).
        String readString;
        System.out.println("Movement: Update state ...");
        if(uart != null) {
            System.out.println("Movement: sending '#S'");
            uart.writeStr("#S");
            do {
                readString = readResponse();
            } while (!readString.startsWith("#S"));
            System.out.println("Movement: reading " + DUMMY_RESPONSE.length() + "bytes");
            readString = uart.readStr(DUMMY_RESPONSE.length());
            System.out.println("Movement: done.");
        } else {
            readString = DUMMY_RESPONSE;
        }
        System.out.println("Movement: Update state: '" + readString + "'");

        // Parse the response.
        String[] segments = readString.split("#");
        String[] servoPositionStrings = segments[1].split(":");
        String[] eyeColorStrings = segments[2].split(":");
        String irSensorString = segments[3];

        // Convert the string values into numeric ones.
        int[] servoPositions = new int[servoPositionStrings.length];
        for(int i = 0; i < servoPositionStrings.length; i++) {
            servoPositions[i] = Integer.valueOf(servoPositionStrings[i]);
        }
        int[] eyeColors = new int[eyeColorStrings.length];
        for(int i = 0; i < eyeColorStrings.length; i++) {
            eyeColors[i] = Integer.valueOf(eyeColorStrings[i]);
        }
        int irSensor = Integer.valueOf(irSensorString);

        // Create a new movement state.
        MovementState movementState = new MovementState(servoPositions, eyeColors, irSensor);

        // Update the current state (and send the changes to the client.
        setMovementState(movementState);
    }

    private String readResponse() {
        StringBuffer sb = new StringBuffer();
        String readChar;
        do {
            readChar = uart.readStr(1);
            System.out.print(readChar);
            sb.append(readChar);
        } while (!"\n".equals(readChar));
        return sb.toString();
    }

    /**
     * Turn the servos off to avoid having the little chap shiver all the time.
     * @param applicationEvent the spring {@link ContextRefreshedEvent} instance
     */
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        System.out.println("Movement: Turn the servos off");
        if(uart != null) {
            uart.writeStr("#H");
        }
    }

}
