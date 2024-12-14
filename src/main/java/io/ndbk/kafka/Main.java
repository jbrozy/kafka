package io.ndbk.kafka;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Config config = new Config();

        log.info("WebSocket: {}", config.getBinanceUrl());
        log.info("Kafka Bootstrap: {}, Topic: {}", config.getKafkaBootstrap(), config.getKafkaTopic());

        try (var producer = KafkaProducerFactory.createProducer(config.getKafkaBootstrap())) {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean shuttingDown = new AtomicBoolean(false);

            BinanceWebSocketClient wsClient = new BinanceWebSocketClient(
                    new URI(config.getBinanceUrl()), producer, config.getKafkaTopic()
            );

            wsClient.connect();

            // Shutdown-Hook für sauberes Beenden
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (shuttingDown.compareAndSet(false, true)) {
                    log.info("Shutdown-Signal empfangen. Schließe Ressourcen...");
                    try {
                        wsClient.close();
                        producer.flush();
                    } catch (Exception e) {
                        log.error("Fehler beim Shutdown", e);
                    } finally {
                        latch.countDown();
                    }
                }
            }));

            latch.await();
            log.info("Beende Binance-zu-Kafka Bridge. Auf Wiedersehen!");
        } catch (Exception e) {
            log.error("Fehler in der Hauptschleife", e);
        }
    }
}

