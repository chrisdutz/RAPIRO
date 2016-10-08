package de.codecentric.iot.rapiro.voice.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 08.10.16.
 */
@Component
@Profile("raspberry")
public class RaspberryVoiceAdapter extends AbstractVoiceAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RaspberryVoiceAdapter.class);

    public RaspberryVoiceAdapter() {
        super();
        LOG.info("Voice: Running in 'raspberry' mode");
    }

    @Override
    protected String getAudioDeviceName() {
        return "plughw:0,1";
    }

}
