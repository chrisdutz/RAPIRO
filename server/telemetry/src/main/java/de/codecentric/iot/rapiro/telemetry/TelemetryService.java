package de.codecentric.iot.rapiro.telemetry;

import de.codecentric.iot.rapiro.telemetry.model.TelemetryData;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.messaging.MessageTemplate;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by christoferdutz on 07.05.16.
 */
@Service("telemetryService")
@RemotingDestination
public class TelemetryService implements InitializingBean {

    private static final String SERVICE_DESTINATION = "telemetryEvents";

    private static final int TOTAL_TIME_UPDATE_LIMIT = 2000;

    @Autowired
    private MessageBroker broker;

    @Autowired
    private MessageTemplate template;

    private final Sigar sigar;
    private final int cpuCount;
    private final long pid;
    private ProcCpu prevPc;

    private TelemetryData telemetryData;
    private double load = 0;

    public TelemetryService() {
        sigar = new Sigar();
        try {
            cpuCount = sigar.getCpuList().length;
            pid = sigar.getPid();
            prevPc = sigar.getProcCpu(pid);
        } catch (SigarException e) {
            throw  new RuntimeException("Telemetry: Caught exception while initializing Sigar library.", e);
        }
    }

    /**
     * Manually create a messaging destination 'visionEvents' for this service.
     */
    @Override
    public void afterPropertiesSet() {
        flex.messaging.services.Service service = broker.getService("messaging-service");
        Destination visionEvents = service.createDestination(SERVICE_DESTINATION);
        service.addDestination(visionEvents);
        service.start();
    }

    public double getCpuLoad() {
        return load;
    }

    @Scheduled(fixedRate = 100)
    public void updateTelemetryData() {
        try {
            ProcCpu curPc = sigar.getProcCpu(pid);
            long totalDelta = curPc.getTotal() - prevPc.getTotal();
            long timeDelta = curPc.getLastTime() - prevPc.getLastTime();
            if (totalDelta == 0) {
                if (timeDelta > TOTAL_TIME_UPDATE_LIMIT) {
                    load = 0;
                }
                if (load == 0) {
                    prevPc = curPc;
                }
            } else {
                load = 100. * totalDelta / timeDelta / cpuCount;
                prevPc = curPc;
                template.send(SERVICE_DESTINATION, load);
            }
        } catch (SigarException ex) {
            throw new RuntimeException(ex);
        }
    }

}
