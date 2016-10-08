package de.codecentric.iot.rapiro.voice.adapter;

import java.io.File;

/**
 * Created by christoferdutz on 08.10.16.
 */
public interface VoiceAdapter {

    void playFile(String file);
    void stop();

}
