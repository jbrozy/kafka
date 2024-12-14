package io.ndbk.kafka;

public class Config {
    private final String binanceUrl;
    private final String kafkaBootstrap;
    private final String kafkaTopic;

    public Config() {
        this.binanceUrl = System.getenv().getOrDefault("BINANCE_WS_URL", "wss://stream.binance.com:9443/ws/btcusdt@trade");
        this.kafkaBootstrap = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "138.201.203.245:9092");
        this.kafkaTopic = System.getenv().getOrDefault("KAFKA_TARGET_TOPIC", "binance-trades");
    }

    public String getBinanceUrl() { return binanceUrl; }
    public String getKafkaBootstrap() { return kafkaBootstrap; }
    public String getKafkaTopic() { return kafkaTopic; }
}
