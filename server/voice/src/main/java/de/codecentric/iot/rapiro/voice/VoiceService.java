package de.codecentric.iot.rapiro.voice;

import jaco.mp3.player.MP3Player;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by christoferdutz on 19.04.16.
 */
@Service("voiceService")
@RemotingDestination
public class VoiceService implements ApplicationListener<ContextRefreshedEvent> {

    private MP3Player player;

    public VoiceService() {
        System.out.println("Voice: Initialized");
    }

    public void startSinging() {
        // If he's already singing, let him stop first.
        if(player != null) {
            stopSinging();
        }

        System.out.println("Voice: Start Singing");
        // Start singing.
        try{
            player = new MP3Player(new URL("classpath:audio/minions-banana.mp3"));
            player.play();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void stopSinging() {
        System.out.println("Voice: Stop Singing");
        if(player != null) {
            player.stop();
            player = null;
        }
    }

    /**
     * Play a "hellow" sample as indicator tha the application is finished loading.
     * @param applicationEvent
     */
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        // Play a "hellow" sample as indicator that
        // the application is finished loading.
        try {
            new MP3Player(new URL("classpath:audio/minions-hellow.mp3")).play();
        } catch (MalformedURLException e) {
            // Ignore ...
        }
    }

}
