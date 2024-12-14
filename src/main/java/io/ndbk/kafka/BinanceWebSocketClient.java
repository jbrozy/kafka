package io.ndbk.kafka;

import java.net.URI;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinanceWebSocketClient extends WebSocketClient {
    private static final Logger log = LoggerFactory.getLogger(BinanceWebSocketClient.class);
    private final KafkaProducer<String, String> producer;
    private final String topic;

    public BinanceWebSocketClient(URI serverUri, KafkaProducer<String, String> producer, String topic) {
        super(serverUri);
        this.producer = producer;
        this.topic = topic;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("Mit Binance WebSocket verbunden.");
    }

    @Override
    public void onMessage(String message) {
        log.info("Message von Binance empfangen: {}", message);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, message);
        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Nachricht an Topic={} Partition={} Offset={} gesendet",
                        metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                log.error("Fehler beim Senden an Kafka", exception);
            }
        });
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Binance WebSocket geschlossen: Code={} Grund={} Remote={}", code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        log.error("Fehler in der WebSocket-Verbindung", ex);
    }
}

