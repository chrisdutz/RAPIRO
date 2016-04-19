package de.codecentric.iot.rapiro;

/**
 * Created by christoferdutz on 19.04.16.
 */
public class MraaSystem {

    private static IotMode mode;

    /**
     * Load the mraajava library used to access Edison hardware I/O.
     */
    static {
        try {
            System.loadLibrary("mraajava");
            mode = IotMode.REAL_MODE;
        } catch (UnsatisfiedLinkError e) {
            mode = IotMode.SIMULATED_MODE;
        }
    }

    public static IotMode getMode() {
        return mode;
    }

}
