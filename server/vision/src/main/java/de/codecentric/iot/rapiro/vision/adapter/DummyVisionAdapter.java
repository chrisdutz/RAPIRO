package de.codecentric.iot.rapiro.vision.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 08.10.16.
 */
@Component
@Profile("dummy")
public class DummyVisionAdapter implements VisionAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(DummyVisionAdapter.class);

    public DummyVisionAdapter() {
        LOG.info("Vision: Running in 'dummy' mode");
    }

    @Override
    public byte readByte() {
        return 0;
    }

    @Override
    public int readWord() {
        return 0;
    }

}
