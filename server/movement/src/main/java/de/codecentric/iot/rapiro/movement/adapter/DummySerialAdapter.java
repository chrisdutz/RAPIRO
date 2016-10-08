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

    private static final Logger LOG = LoggerFactory.getLogger(EdisonSerialAdapter.class);
    private static final String DUMMY_RESPONSE = "#S090091004130090180044090094088086094:000000000000032640:000432\n";

    public DummySerialAdapter() {
        LOG.info("Movement: Running in 'dummy' mode");
    }

    @Override
    public void send(String command) {
        LOG.info("Sending: " + command);
    }

    @Override
    public String read(int length) {
        return DUMMY_RESPONSE;
    }

}
