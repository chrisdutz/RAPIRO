package de.codecentric.iot.rapiro;

/**
 * Created by christoferdutz on 19.04.16.
 */
public class SystemMode {

    private static Mode mode;

    /**
     * Load the mraajava library used to access Edison hardware I/O.
     */
    static {
        try {
            System.loadLibrary("mraajava");
            mode = Mode.REAL_MODE;
        } catch (UnsatisfiedLinkError e) {
            mode = Mode.SIMULATED_MODE;
        }
    }

    public static Mode getMode() {
        return mode;
    }

    public static boolean isSimulatedMode() {
        return mode == Mode.SIMULATED_MODE;
    }

    public static boolean isRealMode() {
        return mode == Mode.REAL_MODE;
    }

}
