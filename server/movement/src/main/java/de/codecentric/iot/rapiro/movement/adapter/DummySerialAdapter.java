package de.codecentric.iot.rapiro.movement.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 07.10.16.
 */
@Component
@Profile("dummy")
public class DummySerialAdapter implements SerialAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(DummySerialAdapter.class);

    private byte[] protocol;
    private int pos;

    public DummySerialAdapter() {
        protocol = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xAA, (byte) 0x55,
                (byte) 0x01, (byte) 0x76, (byte) 0x00, (byte) 0x02,
                (byte) 0x00, (byte) 0x8F, (byte) 0x00, (byte) 0x96,
                (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x1F,
                (byte) 0xAA, (byte) 0x55, (byte) 0x01, (byte) 0x45,
                (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x77,
                (byte) 0x00, (byte) 0xBE, (byte) 0x00, (byte) 0x0C,
                (byte) 0x00, (byte) 0x02};
        pos = -1;

        LOG.info("Movement: Running in 'dummy' mode");
    }

    @Override
    public void send(String command) {
        LOG.info("Sending: " + command);
    }

    @Override
    public byte readByte() {
        if(pos == protocol.length - 1) {
            pos = -1;
        }
        return protocol[++pos];
    }

    @Override
    public int readWord() {
        return ((readByte() & 0xff) << 8) | (readByte() & 0xff);
    }

}
