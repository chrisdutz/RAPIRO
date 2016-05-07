package test.orientation;

import mraa.I2c;
import mraa.I2cMode;
import upm_lsm9ds0.LSM9DS0;

/**
 * Created by christoferdutz on 02.05.16.
 */
public class OrentationTest {

    static {
        try {
            System.loadLibrary("mraajava");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public OrentationTest() {
        I2c am = new I2c(0);
        am.address((short) 0x1D);
        short whoAmIAm = am.readReg((short) 0x0F);
        if(whoAmIAm == (short) 0x00)
        System.out.println("AM: " + whoAmIAm);
    }

    public static void main(String[] args) throws Exception {
        new OrentationTest();
    }

}
