package de.codecentric.iot.rapiro.voice.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by christoferdutz on 08.10.16.
 */
public abstract class AbstractVoiceAdapter implements VoiceAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractVoiceAdapter.class);

    private Mixer.Info outputDevice;
    private Clip currentClip;

    public AbstractVoiceAdapter() {
        String audioDeviceName = getAudioDeviceName();
        // Find the audio out device for the current platform
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        for(Mixer.Info mixerInfo : info) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("- Available mixer device: " + mixerInfo.getName());
            }
            if (mixerInfo.getName().contains(audioDeviceName)) {
                outputDevice = mixerInfo;
                LOG.info("- Found Mixer, Audio system enabled.");
            }
        }

        if(outputDevice != null) {
            initAudioDevice();
            LOG.info("Voice: Initialized");
        } else {
            LOG.error("Voice: Failed initializing");
            // TODO: Throw an error ...
        }
    }

    protected abstract String getAudioDeviceName();

    protected void initAudioDevice() {
        // Do nothing
    }

    public void playFile(String filename) {
        if(currentClip != null) {
            currentClip.stop();
        }

        try {
            InputStream audioSrc = getClass().getResourceAsStream(filename);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream sound = AudioSystem.getAudioInputStream(bufferedIn);
            DataLine.Info dlInfo = new DataLine.Info(Clip.class, sound.getFormat());
            Mixer mixer = AudioSystem.getMixer(outputDevice);
            currentClip = (Clip) mixer.getLine(dlInfo);
            currentClip.open(sound);

            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                }
            });

            // play the sound clip
            currentClip.start();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        if(currentClip != null) {
            currentClip.stop();
        }
    }

}
