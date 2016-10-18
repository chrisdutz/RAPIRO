package de.codecentric.iot.rapiro.telemetry.actors;

import de.codecentric.iot.rapiro.akka.actors.AbstractActor;
import de.codecentric.iot.rapiro.telemetry.model.TelemetryData;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
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

    private static final int TOTAL_TIME_UPDATE_LIMIT = 2000;

    private final Sigar sigar;
    private final int cpuCount;
    private final long pid;
    private ProcCpu prevCpu;

    private double cpuLoad = 0;

    public TelemetryActor() {
        super();

        sigar = new Sigar();
        try {
            cpuCount = sigar.getCpuList().length;
            pid = sigar.getPid();
            prevCpu = sigar.getProcCpu(pid);
        } catch (SigarException e) {
            throw  new RuntimeException("Telemetry: Caught exception while initializing Sigar library.", e);
        }

    }

    @Override
    public void onNext(TelemetryData element) {
        super.onNext(element);
    }

    @Override
    protected List<TelemetryData> getItems() {
        try {
            ProcCpu curCpu = sigar.getProcCpu(pid);
            long totalDelta = curCpu.getTotal() - prevCpu.getTotal();
            long timeDelta = curCpu.getLastTime() - prevCpu.getLastTime();
            if (totalDelta == 0) {
                if (timeDelta > TOTAL_TIME_UPDATE_LIMIT) {
                    cpuLoad = 0;
                }
                if (cpuLoad == 0) {
                    prevCpu = curCpu;
                }
            } else {
                cpuLoad = 100. * totalDelta / timeDelta / cpuCount;
                prevCpu = curCpu;
            }

            ProcMem curMem = sigar.getProcMem(pid);

            TelemetryData telemetryData = new TelemetryData();
            telemetryData.setCpuLoad(cpuLoad);
            telemetryData.setMemoryUsage(curMem.getSize());
            return Collections.singletonList(telemetryData);

        } catch (SigarException ex) {
            throw new RuntimeException(ex);
        }
    }

}
