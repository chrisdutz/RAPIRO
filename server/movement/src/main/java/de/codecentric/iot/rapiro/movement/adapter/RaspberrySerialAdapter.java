package de.codecentric.iot.rapiro.movement.adapter;

import com.pi4j.io.serial.*;
import com.pi4j.io.serial.impl.SerialImpl;
import org.apache.commons.collections4.queue.CircularFifoQueue;
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
    private CircularFifoQueue<Byte> buffer;

    public RaspberrySerialAdapter() {
        uart = new SerialImpl();
        buffer = new CircularFifoQueue<>();
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
    public int bytesAvailable() {
        return buffer.size();
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
    public byte peekByte() {
        if(!buffer.isEmpty()) {
            return buffer.peek();
        }
        return -1;
    }

    @Override
    public byte readByte() {
        if(!buffer.isEmpty()) {
            return buffer.get(0);
        }
        return -1;
    }

    @Override
    public int readWord() {
        if(buffer.size() >= 2) {
            return ((buffer.get(0) & 0xff) << 8) | (buffer.get(0) & 0xff);
        }
        return -1;
    }

    @Override
    public void addAsyncReader(AsyncReader asyncReader) {
        uart.addListener((SerialDataEventListener) serialDataEvent -> {
            try {
                // Read all the bytes from the serial interface.
                byte[] readBytes = serialDataEvent.getBytes();
                // Add each byte to the buffer.
                for (byte readByte : readBytes) {
                    buffer.add(readByte);
                }
                // Tell the client that new Data is available.
                asyncReader.dataAvailable();
            } catch (IOException e) {
                LOG.error("Error reading bytes from serial port", e);
            }
        });
    }

}
