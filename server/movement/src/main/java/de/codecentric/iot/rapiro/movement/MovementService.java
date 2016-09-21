package de.codecentric.iot.rapiro.movement;

import de.codecentric.iot.rapiro.SystemMode;
import mraa.Result;
import mraa.Uart;
import mraa.UartParity;
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

    private Uart uart;

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
