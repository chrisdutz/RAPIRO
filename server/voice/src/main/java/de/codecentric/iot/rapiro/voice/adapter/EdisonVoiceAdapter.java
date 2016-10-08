package de.codecentric.iot.rapiro.voice.adapter;

import de.codecentric.iot.rapiro.voice.utils.WM8958;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by christoferdutz on 08.10.16.
 */
@Component
@Profile("edison")
public class EdisonVoiceAdapter extends AbstractVoiceAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(EdisonVoiceAdapter.class);

    private WM8958 wm8958;

    public EdisonVoiceAdapter() {
        super();
        LOG.info("Voice: Running in 'edison' mode");
    }

    @Override
    protected String getAudioDeviceName() {
        return "plughw:1,0";
    }

    @Override
    protected void initAudioDevice() {
        wm8958 = new WM8958();
        wm8958.reset();
    }

}
