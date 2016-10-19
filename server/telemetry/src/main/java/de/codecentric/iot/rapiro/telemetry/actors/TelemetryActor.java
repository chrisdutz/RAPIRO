package de.codecentric.iot.rapiro.telemetry.actors;

import de.codecentric.iot.rapiro.akka.actors.AbstractActor;
import de.codecentric.iot.rapiro.telemetry.model.TelemetryData;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Created by christoferdutz on 18.10.16.
 */
@Scope("prototype")
@Component("telemetryActor")
public class TelemetryActor extends AbstractActor<TelemetryData> {

    private final Sigar sigar;
    private final long pid;

    public TelemetryActor() {
        super();

        sigar = new Sigar();
        pid = sigar.getPid();
    }

    @Override
    public void onNext(TelemetryData element) {
        super.onNext(element);
    }

    @Override
    protected List<TelemetryData> getItems() {
        try {
            CpuPerc cpuPerc = sigar.getCpuPerc();
            Mem mem = sigar.getMem();
            TelemetryData telemetryData = new TelemetryData();
            telemetryData.setCpuLoad(cpuPerc.getCombined());
            telemetryData.setMemoryUsage((double) mem.getUsed() / (double) mem.getTotal());
            return Collections.singletonList(telemetryData);

        } catch (SigarException ex) {
            throw new RuntimeException(ex);
        }
    }

}
