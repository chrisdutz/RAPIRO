package test;

import javax.sound.sampled.*;
import java.lang.reflect.Method;

/**
 * Created by christoferdutz on 19.04.16.
 */
public class VoiceTest {

    private VoiceTest() {
        System.out.println("Record Test ...");
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        int i =0;
        for(Mixer.Info mixerInfo : info){
            // Init the sound hardware, if we are on Rapiro
            if(mixerInfo.getName().contains("plughw:1,0")) {
                System.out.println("Configuring WM8958 chip");
                WM8958 wm8958 = new WM8958();
                wm8958.reset();
                System.out.println("Finished configuring WM8958 chip");
            }

            System.out.println("\n\nName: " + mixerInfo.getName() + ", description: " + mixerInfo.getDescription() + "\n");
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            System.out.println("  -----------------------------------");
            System.out.println("   Source Lines");
            System.out.println("  -----------------------------------");
            for(Line.Info lineInfo : mixer.getSourceLineInfo()) {
                System.out.println("   Line.Info: " + lineInfo.toString());
                try {
                    Line line = mixer.getLine(lineInfo);
                    Method method = line.getClass().getMethod("getFormat");
                    method.setAccessible(true);
                    AudioFormat format = (AudioFormat) method.invoke(line);
                    System.out.println("    - Format: Sample Rate: " + format.getSampleRate() + ", Frame Rate: " + format.getFrameRate() + ", Frame Size: " + format.getFrameSize() + ", Sample Size (bits): " + format.getSampleSizeInBits() + ", Channels: " + format.getChannels() + ", Big Endian: " + format.isBigEndian() + ", Encoding: " + format.getEncoding().toString());
                } catch (Exception e) {
                    // Ignore..
                }
            }
            System.out.println("  -----------------------------------");
            System.out.println("   Target Lines");
            System.out.println("  -----------------------------------");
            for(Line.Info lineInfo : mixer.getTargetLineInfo()) {
                System.out.println("   Line.Info: " + lineInfo.toString());
                try {
                    Line line = mixer.getLine(lineInfo);
                    Method method = line.getClass().getMethod("getFormat");
                    method.setAccessible(true);
                    AudioFormat format = (AudioFormat) method.invoke(line);
                    System.out.println("    - Format: Sample Rate: " + format.getSampleRate() + ", Frame Rate: " + format.getFrameRate() + ", Frame Size: " + format.getFrameSize() + ", Sample Size (bits): " + format.getSampleSizeInBits() + ", Channels: " + format.getChannels() + ", Big Endian: " + format.isBigEndian() + ", Encoding: " + format.getEncoding().toString());
                } catch (Exception e) {
                    // Ignore..
                }
            }

            // Do the recording
            if(mixerInfo.getName().contains("plughw:1,0") || mixerInfo.getName().contains("Default Audio Device")) {
                System.out.println("Starting recorder ...");
                SoundRecorder recorder = new SoundRecorder();
                recorder.record(mixerInfo);
            }
            i++;
        }
    }

    public static void main(String[] args) throws Exception {
        new VoiceTest();
    }

}
