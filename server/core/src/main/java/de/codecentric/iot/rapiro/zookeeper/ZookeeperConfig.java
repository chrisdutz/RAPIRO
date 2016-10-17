package de.codecentric.iot.rapiro.zookeeper;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Little helper to fire up an embedded zookeeper instance, which kafka can use to
 * communicate.
 */
@Configuration
@Profile("zookeeper")
public class ZookeeperConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperConfig.class);

    public static final int ZOOKEEPER_SERVER_PORT = 2181;

    @Bean
    public ZooKeeperServerMain zooKeeperServer() {
        LOG.info("-----------------------------------------------");
        LOG.info("Initializing ZooKeeper system");
        LOG.info("-----------------------------------------------");

        Properties startupProperties = new Properties();
        startupProperties.setProperty("tickTime", "2000");
        startupProperties.setProperty("dataDir",
                new File(System.getProperty("java.io.tmpdir"), "rapiro-zookeeper").getAbsolutePath());
        startupProperties.setProperty("clientPort", Integer.toString(ZOOKEEPER_SERVER_PORT));

        QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
        try {
            quorumConfiguration.parseProperties(startupProperties);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        final ServerConfig configuration = new ServerConfig();
        configuration.readFrom(quorumConfiguration);

        ZooKeeperServerMain zooKeeperInstance = new ZooKeeperServerMain();
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

        LOG.info("ZooKeeper system initialized");

        return zooKeeperInstance;
    }

}
