package de.codecentric.iot.rapiro.zookeeper;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Little helper to fire up an embedded zookeeper instance, which kafka can use to
 * communicate.
 */
@Configuration
public class ZookeeperConfig implements ApplicationListener {

    public static final int ZOOKEEPER_CLIENT_PORT = 8081;

    private ZooKeeperServerMain zooKeeperInstance = new ZooKeeperServerMain();

    public ZooKeeperServerMain zooKeeperInstance() {
        return zooKeeperInstance;
    }

    /**
     * As soon as the spring context is fully initialized (an instance of type
     * ContextRefreshedEvent is received), fire up the ZooKeeper instance.
     *
     * @param event the spring application event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            Properties startupProperties = new Properties();
            startupProperties.setProperty("tickTime", "2000");
            startupProperties.setProperty("dataDir",
                    new File(System.getProperty("java.io.tmpdir"), "rapiro-zookeeper").getAbsolutePath());
            startupProperties.setProperty("clientPort", Integer.toString(ZOOKEEPER_CLIENT_PORT));

            QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
            try {
                quorumConfiguration.parseProperties(startupProperties);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }

            final ServerConfig configuration = new ServerConfig();
            configuration.readFrom(quorumConfiguration);

            new Thread() {
                public void run() {
                    try {
                        zooKeeperInstance.runFromConfig(configuration);
                    } catch (IOException e) {
                        System.err.println("ZooKeeper Failed");
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

}
