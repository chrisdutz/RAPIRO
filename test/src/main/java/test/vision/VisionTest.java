package test.vision;

import mraa.Result;
import mraa.Spi;
import mraa.Uart;
import mraa.UartParity;

/**
 * Created by christoferdutz on 02.05.16.
 */
public class VisionTest {

    static {
        try {
            System.loadLibrary("mraajava");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private Spi spi;

    public VisionTest() {
        spi = new Spi(0);
        while(true) {
            int word = readWord();
            System.out.println(  String.format("%04X", word & 0xFFFF));
        }
    }

    private int readWord() {
        int data = spi.write_word(0);
        return Integer.reverseBytes(data) >> 16 & 0x0000FFFF;
    }


    public static void main(String[] args) throws Exception {
        new VisionTest();
    }

}
