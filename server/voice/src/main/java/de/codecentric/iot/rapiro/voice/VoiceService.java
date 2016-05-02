package de.codecentric.iot.rapiro.voice;

import de.codecentric.iot.rapiro.SystemMode;
import de.codecentric.iot.rapiro.voice.utils.SoundRecorder;
import de.codecentric.iot.rapiro.voice.utils.WM8958;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by christoferdutz on 19.04.16.
 */
@Service("voiceService")
@RemotingDestination
public class VoiceService implements ApplicationListener<ContextRefreshedEvent> {

    private WM8958 wm8958;
    private Mixer.Info outputDevice;
    private SoundRecorder recorder;

    private Clip currentClip;

    public VoiceService() {
        // Find the audio out device for the current platform
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        for(Mixer.Info mixerInfo : info) {
            if (SystemMode.isRealMode()) {
                if (mixerInfo.getName().contains("plughw:1,0")) {
                    outputDevice = mixerInfo;
                }
            } else {
                if (mixerInfo.getName().contains("Default Audio Device")) {
                    outputDevice = mixerInfo;
                }
            }
        }

        if(outputDevice != null) {
            if(SystemMode.isRealMode()) {
                wm8958 = new WM8958();
                wm8958.reset();
            }
            System.out.println("Voice: Initialized");
        } else {
            System.out.println("Voice: Failed initializing");
        }
    }

    public void startSinging() {
        System.out.println("Voice: Start singing");
        playFile("/audio/minions-banana.wav");
    }

    public void stopSinging() {
        System.out.println("Voice: Stop singing");
        if(currentClip != null) {
            currentClip.stop();
        }
    }

    private void playFile(String filename) {
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

    /**
     * Play a "hellow" sample as indicator tha the application is finished loading.
     * @param applicationEvent the spring {@link ContextRefreshedEvent} instance
     */
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        System.out.println("Voice: Play start sound");
        playFile("/audio/minions-hellow.wav");

        // TODO: Test recording...
        /*recorder = new SoundRecorder();
        recorder.record(outputDevice);*/
    }

}
