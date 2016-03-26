package de.codecentric.iot.rapiro.movement;

import mraa.Result;
import mraa.Uart;
import mraa.UartParity;
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
        uart = new Uart("/dev/ttyMFD1");

        if (uart.setBaudRate(57600) != Result.SUCCESS) {
            System.err.println("UART: Error setting baud rate");
        }

        /*if (uart.setMode(8, UartParity.UART_PARITY_NONE, 1) != Result.SUCCESS) {
            System.err.println("UART: Error setting mode");
        }

        if (uart.setFlowcontrol(false, false) != Result.SUCCESS) {
            System.err.println("UART: Error setting flow control");
        }*/
    }

    public void stop() {
        uart.writeStr("#M0");
    }

    public void moveForward() {
        uart.writeStr("#M1");
    }

    public void moveLeft() {
        System.out.println("Left");
    }

    public void moveRight() {
        System.out.println("Right");
    }

    public void moveBack() {
        System.out.println("Back");
    }

}
