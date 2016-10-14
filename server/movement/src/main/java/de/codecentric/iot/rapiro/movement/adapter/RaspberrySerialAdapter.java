package de.codecentric.iot.rapiro.movement.adapter;

import com.pi4j.io.serial.*;
import com.pi4j.io.serial.impl.SerialImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by christoferdutz on 23.03.16.
 */
@Component
@Profile("raspberry")
public class RaspberrySerialAdapter implements SerialAdapter {

    static private final Logger LOG = LoggerFactory.getLogger(RaspberrySerialAdapter.class);

    private Serial uart;

    public RaspberrySerialAdapter() {
        uart = new SerialImpl();
        try {
            uart.open("/dev/ttyS0", Baud._57600, DataBits._8,
                    Parity.NONE, StopBits._1, FlowControl.NONE);
        } catch (IOException e) {
            LOG.error("Error initializing serial io", e);
            // TODO: Throw an error
        }
        LOG.info("Movement: Running in 'raspberry' mode");
    }

    @Override
    public void send(String command) {
        try {
            uart.write(command);
        } catch (IOException e) {
            LOG.error("Error sending the command '" + command + "' to the serial port.");
        }
    }

    @Override
    public String read(int length) {
        try {
            return new String(uart.read(length));
        } catch (IOException e) {
            LOG.error("Caught exception while reading from serial port", e);
            return null;
        }
    }

}
