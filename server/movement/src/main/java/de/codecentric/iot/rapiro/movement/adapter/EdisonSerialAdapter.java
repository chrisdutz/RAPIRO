package de.codecentric.iot.rapiro.movement.adapter;

import mraa.Result;
import mraa.Uart;
import mraa.UartParity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 23.03.16.
 */
@Component
@Profile("edison")
public class EdisonSerialAdapter implements SerialAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(EdisonSerialAdapter.class);

    private Uart uart;

    public EdisonSerialAdapter() {
        uart = new Uart("/dev/ttyMFD1");

        if (uart.setBaudRate(57600) != Result.SUCCESS) {
            LOG.error("UART: Error setting baud rate");
            // TODO: Throw an error
        }

        if (uart.setMode(8, UartParity.UART_PARITY_NONE, 1) != Result.SUCCESS) {
            LOG.error("UART: Error setting mode");
            // TODO: Throw an error
        }

        if (uart.setFlowcontrol(false, false) != Result.SUCCESS) {
            LOG.error("UART: Error setting flow control");
            // TODO: Throw an error
        }
        LOG.info("Movement: Running in 'edison' mode");
    }

    @Override
    public void send(String command) {
        uart.writeStr("#M0");
    }

    @Override
    public String read(int length) {
        return uart.readStr(length);
    }

}
