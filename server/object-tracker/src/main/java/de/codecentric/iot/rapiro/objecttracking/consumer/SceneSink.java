package de.codecentric.iot.rapiro.objecttracking.consumer;

import de.codecentric.iot.rapiro.vision.model.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by christoferdutz on 14.10.16.
 */
public class SceneSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(SceneSink.class);

    public SceneSink() {
    }

    public void logMessage(String scene) {
        LOGGER.info("Scene: " + scene);
    }

}
