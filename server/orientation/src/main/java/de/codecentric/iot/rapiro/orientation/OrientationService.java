package de.codecentric.iot.rapiro.orientation;

import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import upm_lsm9ds0.LSM9DS0;

import java.util.Arrays;

/**
 * Created by christoferdutz on 02.05.16.
 */
@Service("orientationService")
@RemotingDestination
public class OrientationService {

    private LSM9DS0 lsm9DS0;

    public OrientationService() {
        lsm9DS0 = new LSM9DS0();
        lsm9DS0.init();
        short accelerometerStatus = lsm9DS0.getAccelerometerStatus();
        System.out.println("Accelerometer Status: " + accelerometerStatus);
        short gyroscopeStatus = lsm9DS0.getGyroscopeStatus();
        System.out.println("Gyroscope Status: " + gyroscopeStatus);
        short magnetometerStatus = lsm9DS0.getMagnetometerStatus();
        System.out.println("Magnetometer Status: " + magnetometerStatus);
    }

    @Scheduled(fixedRate = 100)
    public void updateOrientationData() {
        lsm9DS0.update();
        System.out.println("--------------------------------------------");
        System.out.println("Accelerometer: " + Arrays.toString(lsm9DS0.getAccelerometer()));
        System.out.println("Gyroscope    : " + Arrays.toString(lsm9DS0.getGyroscope()));
        System.out.println("Magnetometer : " + Arrays.toString(lsm9DS0.getMagnetometer()));
        System.out.println("Temperature  : " + lsm9DS0.getTemperature());
    }

}
