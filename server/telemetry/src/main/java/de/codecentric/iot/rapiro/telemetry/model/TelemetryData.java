package de.codecentric.iot.rapiro.telemetry.model;

/**
 * Created by christoferdutz on 07.05.16.
 */
public class TelemetryData {

    private double cpuLoad;
    private long memoryUsage;

    public TelemetryData() {
    }

    public TelemetryData(double cpuLoad, long memoryUsage) {
        this.cpuLoad = cpuLoad;
        this.memoryUsage = memoryUsage;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public long getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(long memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
}
