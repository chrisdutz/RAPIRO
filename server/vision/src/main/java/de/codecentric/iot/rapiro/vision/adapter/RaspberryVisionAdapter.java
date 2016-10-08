package de.codecentric.iot.rapiro.vision.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 08.10.16.
 */
@Component
@Profile("raspberry")
public class RaspberryVisionAdapter implements VisionAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RaspberryVisionAdapter.class);

    public RaspberryVisionAdapter() {
        LOG.info("Vision: Running in 'raspberry' mode");
    }

    @Override
    public byte readByte() {
        // TODO: Implement ...
        return 0;
    }

    @Override
    public int readWord() {
        // TODO: Implement ...
        return 0;
    }

}
