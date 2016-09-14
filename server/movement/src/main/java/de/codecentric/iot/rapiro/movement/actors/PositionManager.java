package de.codecentric.iot.rapiro.movement.actors;

import akka.japi.pf.ReceiveBuilder;
import akka.stream.actor.AbstractActorPublisher;
import akka.stream.actor.ActorPublisherMessage;
import de.codecentric.iot.rapiro.SystemMode;
import de.codecentric.iot.rapiro.movement.model.PositionProtocol;
import mraa.Result;
import mraa.Uart;
import mraa.UartParity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christoferdutz on 12.09.16.
 */
@Component("movementManager")
@Scope("prototype")
public class PositionManager extends AbstractActorPublisher<PositionProtocol.Position> {

    private static final String DUMMY_RESPONSE = "#S090091004130090180044090094088086094:000000000000032640:000432\n";

    private final int MAX_BUFFER_SIZE = 100;

    private Uart uart;

    private final List<PositionProtocol.Position> buf = new ArrayList<>();

    public PositionManager() {
        // Initialize the serial port for communication with the Arduino
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

        // Initialize the akka stuff.
        receive(ReceiveBuilder.
                // If a position request is coming in and the number of requests in
                // the queue exceeds the buffer size, return with an error response.
                match(PositionProtocol.Position.class, positionRequest -> buf.size() == MAX_BUFFER_SIZE, positionRequest -> {
                    sender().tell(PositionProtocol.UpdatePositionDenied, self());
                }).
                // If a position request is coming in and the queue is not full,
                // respond with an acknowledge response.
                match(PositionProtocol.Position.class, job -> {
                    sender().tell(PositionProtocol.UpdatePositionAccepted, self());

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
    }

    private void deliverBuf() {
        while (totalDemand() > 0) {
            // Get max "totalDemand" number of elements from the queue and send them back.
            if (totalDemand() <= Integer.MAX_VALUE) {
                final List<PositionProtocol.Position> took =
                        buf.subList(0, Math.min(buf.size(), (int) totalDemand()));
                took.forEach(this::onNext);
                buf.removeAll(took);
                break;
            }
            // If "totalDemand" was greater than the maximum int value, just return
            // max-int results and have the next batch delivered next time.
            else {
                final List<PositionProtocol.Position> took =
                        buf.subList(0, Math.min(buf.size(), Integer.MAX_VALUE));
                took.forEach(this::onNext);
                buf.removeAll(took);
            }
        }
    }

    @Override
    public void onNext(PositionProtocol.Position element) {
        updatePositionData(element);
        super.onNext(element);
    }

    private void updatePositionData(PositionProtocol.Position element) {
        // Read the state from the Arduino (or simulate in simulation mode).
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

        // Set the values in the position element.
        element.setServoPositions(servoPositions);
        element.setEyeColors(eyeColors);
        element.setIrDistance(irSensor);
    }

    private String readResponse() {
        StringBuilder sb = new StringBuilder();
        String readChar;
        do {
            readChar = uart.readStr(1);
            System.out.print(readChar);
            sb.append(readChar);
        } while (!"\n".equals(readChar));
        return sb.toString();
    }

}
