package de.codecentric.iot.rapiro.kafka;

import de.codecentric.iot.rapiro.zookeeper.ZookeeperConfig;
import kafka.server.KafkaServer;
import kafka.utils.Time;
import org.apache.commons.lang3.SystemUtils;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scala.Option$;

import java.io.File;
import java.util.Properties;
import java.util.Random;

/**
 * Little helper to setup kafka.
 */
@Configuration
public class KafkaConfig {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConfig.class);

    public static final int KAFKA_SERVER_PORT = 9092;

    private static final Random RANDOM = new Random();

    @Bean
    public KafkaServer kafkaServer(ZooKeeperServerMain zooKeeperServer) {
        LOG.info("-----------------------------------------------");
        LOG.info("Initializing Kafka system");
        LOG.info("-----------------------------------------------");

        File logDir = constructTempDir("kafka-local");

        Properties properties = new Properties();
        properties.setProperty("zookeeper.connect", "localhost:" + ZookeeperConfig.ZOOKEEPER_SERVER_PORT);
        properties.setProperty("broker.id", "1");
        properties.setProperty("default.replication.factor", "1");
        properties.setProperty("host.name", "localhost");
        properties.setProperty("port", Integer.toString(KAFKA_SERVER_PORT));
        properties.setProperty("log.dir", logDir.getAbsolutePath());
        properties.setProperty("log.flush.interval.messages", String.valueOf(1));

        kafka.server.KafkaConfig config = new kafka.server.KafkaConfig(properties);
        KafkaServer server = new KafkaServer(config, new SystemTime(), Option$.MODULE$.empty());
        server.startup();

        LOG.info("Kafka system initialized");

        return server;
    }

    private static File constructTempDir(String dirPrefix) {
        File file = new File(SystemUtils.getJavaIoTmpDir(), dirPrefix + "-" + RANDOM.nextInt(10000000));
        if (!file.mkdirs()) {
            throw new RuntimeException("could not create temp directory: " + file.getAbsolutePath());
        }
        file.deleteOnExit();
        return file;
    }

    public static class SystemTime implements Time {
        public long milliseconds() {
            return System.currentTimeMillis();
        }

        public long nanoseconds() {
            return System.nanoTime();
        }

        public void sleep(long ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

}
