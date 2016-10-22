package de.codecentric.iot.rapiro.vision.adapter;

import com.pi4j.wiringpi.Spi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by christoferdutz on 08.10.16.
 */
@Component
@Profile("raspberry")
public class RaspberrySpiAdapter implements SpiAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RaspberrySpiAdapter.class);

    private static final byte[] byteBuffer = new byte[1];
    private static final byte[] wordBuffer = new byte[2];

    public RaspberrySpiAdapter() {
        LOG.info("Vision: Running in 'raspberry' mode");
        int res = Spi.wiringPiSPISetupMode(0, 10000000, 0);
        if(res == -1) {
            System.out.println("Error in: Spi.wiringPiSPISetupMode(0, 10000000, 0)");
        }
    }

    @Override
    public byte readByte() {
        byte[] response = readBuffer(byteBuffer);
        return response[0];
    }

    @Override
    public int readWord() {
        byte[] response = readBuffer(wordBuffer);
        return ((response[0] & 0xff) << 8) | (response[1] & 0xff);
    }

    private byte[] readBuffer(byte[] buffer) {
        // Set the buffer to "0x00" for every byte.
        Arrays.fill(buffer, (byte) 0 );

        // Read buffer.length bytes.
        int res = Spi.wiringPiSPIDataRW(0, buffer);

        // Check the return code.
        if(res <= -1) {
            System.out.println("Error in: Spi.wiringPiSPIDataRW(0, buffer)");
        }

        // Return the buffer.
        return buffer;
    }

}
