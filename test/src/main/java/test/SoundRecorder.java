package test;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by christoferdutz on 24.04.16.
 */
public class SoundRecorder {
    // record duration, in milliseconds
    static final long RECORD_TIME = 10000;  // 1 minute

    // path of the wav file
    File wavFile = new File("RecordAudio.wav");

    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    private Mixer.Info outputDevice;

    /**
     * Defines an audio format
     */
    private AudioFormat getAudioFormat() {
        float sampleRate = 48000;
        int sampleSizeInBits = 16;
        int channels = 2;
        int frameSize = 4;
        float frameRate = 48000;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, sampleSizeInBits,
                channels, frameSize, frameRate, bigEndian);
        return format;
    }

    /**
     * Captures the sound and record into a WAV file
     */
    private void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            Mixer mixer = AudioSystem.getMixer(outputDevice);
            if (!mixer.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) mixer.getLine(info);
            line.open(format);
            line.start();

            System.out.println("Start capturing...");

            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Start recording...");

            // start recording
            AudioSystem.write(ais, fileType, wavFile);
            System.out.println("Recording to: " + wavFile.getAbsolutePath());

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    private void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }

    /**
     * Entry to run the program
     */
    public void record(Mixer.Info outputDevice) {
        this.outputDevice = outputDevice;

        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(RECORD_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            finish();
        });

        stopper.start();

        // start recording
        start();
    }
}