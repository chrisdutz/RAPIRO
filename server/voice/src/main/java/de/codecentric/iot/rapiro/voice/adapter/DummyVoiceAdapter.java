package de.codecentric.iot.rapiro.voice.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 08.10.16.
 */
@Component
@Profile("dummy")
public class DummyVoiceAdapter extends AbstractVoiceAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(DummyVoiceAdapter.class);

    public DummyVoiceAdapter() {
        super();
        LOG.info("Voice: Running in 'dummy' mode");
    }

    @Override
    protected String getAudioDeviceName() {
        return "Default Audio Device";
    }

}
