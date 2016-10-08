package de.codecentric.iot.rapiro.vision.adapter;

import mraa.Spi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 08.10.16.
 */
@Component
@Profile("edison")
public class EdisonVisionAdapter implements VisionAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(EdisonVisionAdapter.class);

    private Spi spi;

    public EdisonVisionAdapter() {
        spi = new Spi(0);
        LOG.info("Vision: Running in 'edison' mode");
    }

    @Override
    public byte readByte() {
        return (byte) spi.writeByte((short) 0);
    }

    @Override
    public int readWord() {
        return spi.write_word(0);
    }

}
