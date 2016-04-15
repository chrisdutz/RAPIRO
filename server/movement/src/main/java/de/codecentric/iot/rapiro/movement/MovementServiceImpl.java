package de.codecentric.iot.rapiro.movement;

import mraa.Result;
import mraa.Uart;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

/**
 * Created by christoferdutz on 23.03.16.
 */
@Service("movementService")
@RemotingDestination
public class MovementServiceImpl {

    private Uart uart;

    public MovementServiceImpl() {
        if(Boolean.valueOf(System.getProperty("mraa-loaded", "false"))) {
            uart = new Uart("/dev/ttyMFD1");

            if (uart.setBaudRate(57600) != Result.SUCCESS) {
                System.err.println("UART: Error setting baud rate");
            }

            System.out.println("Movement: Running in real mode");
        } else {
            System.out.println("Movement: Running in simulation mode");
        }
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

}
