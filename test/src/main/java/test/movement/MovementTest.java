package test.movement;

import mraa.I2c;
import mraa.Result;
import mraa.Uart;
import mraa.UartParity;

/**
 * Created by christoferdutz on 02.05.16.
 */
public class MovementTest {

    static {
        try {
            System.loadLibrary("mraajava");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private Uart uart;

    public MovementTest() {
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

        uart.writeStr("#M1");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        uart.writeStr("#H");
        uart.writeStr("#S");
        System.out.println("Response: '" + readResponse() + "'");
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


    public static void main(String[] args) throws Exception {
        new MovementTest();
    }

}
