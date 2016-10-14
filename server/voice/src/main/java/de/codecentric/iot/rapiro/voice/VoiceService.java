package de.codecentric.iot.rapiro.voice;

import de.codecentric.iot.rapiro.voice.adapter.VoiceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

/**
 * Created by christoferdutz on 19.04.16.
 */
@Service("voiceService")
@RemotingDestination
public class VoiceService implements ApplicationListener<ContextRefreshedEvent> {

    static private final Logger LOG = LoggerFactory.getLogger(VoiceService.class);

    @Autowired
    private VoiceAdapter voiceAdapter;

    public void play(String sampleName) {
        LOG.info("Voice: Start playing " + sampleName);
        voiceAdapter.playFile("/audio/" + sampleName);
    }

    public void stop() {
        LOG.info("Voice: Stop playing");
        voiceAdapter.stop();
    }

    /**
     * Play a "hellow" sample as indicator tha the application is finished loading.
     * @param applicationEvent the spring {@link ContextRefreshedEvent} instance
     */
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        LOG.info("Voice: Play start sound");
        play("minions-hellow.wav");
    }

}
